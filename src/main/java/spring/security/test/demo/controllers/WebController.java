package spring.security.test.demo.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.security.test.demo.model.order.Order;
import spring.security.test.demo.model.product.Product;
import spring.security.test.demo.model.token.ResetPasswordToken;
import spring.security.test.demo.model.user.DynamicValues;
import spring.security.test.demo.model.user.User;
import spring.security.test.demo.payload.mail.MailRequest;
import spring.security.test.demo.payload.mail.MailService;
import spring.security.test.demo.payload.request.order.OrderRequest;
import spring.security.test.demo.payload.request.order.OrderRequestSchema;
import spring.security.test.demo.payload.request.order.OrderSchema;
import spring.security.test.demo.payload.request.product.ExistingProductRequest;
import spring.security.test.demo.payload.request.forgotPassword.ForgotPasswordRequest;
import spring.security.test.demo.payload.request.product.ProductRequest;
import spring.security.test.demo.payload.request.product.StockProductCount;
import spring.security.test.demo.payload.request.product.UpdateProductRequest;
import spring.security.test.demo.payload.request.user.DynamicValuesRequest;
import spring.security.test.demo.payload.response.JWTResponse;
import spring.security.test.demo.payload.response.MessageResponse;
import spring.security.test.demo.repos.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff")
public class WebController {

    @Autowired
    UserRepo userRepository;

    @Autowired
    RoleRepo roleRepository;

    @Autowired
    TokenRepo tokenRepository;

    @Autowired
    ProductRepo productRepository;

    @Autowired
    private MailService emailService;

    @Autowired
    OrderRepo orderRepository;


    @GetMapping(value = "/userInfo/{email}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> userDetails(@Valid @PathVariable String email) {

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email doesn't exists!"));
        }

        User values = userRepository.findByEmail(email);
        DynamicValues dynamicValues = new DynamicValues();
        dynamicValues.setBookedNow(values.getBookedNow());
        dynamicValues.setRead(values.getRead());

        return ResponseEntity.ok().body(dynamicValues);
    }


    @PostMapping(value = "/ForgotPassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResetPasswordToken> ForgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {

        val resetPasswordEmail = forgotPasswordRequest.getEmail();

        if (!userRepository.existsByEmail(resetPasswordEmail)) {
            log.error("Sorry but Email doesn't exists!");
            return null;
        }

        if (tokenRepository.existsByEmail(resetPasswordEmail)) {
            ResetPasswordToken resetPasswordToken = tokenRepository.findByEmail(resetPasswordEmail);
            tokenRepository.deleteByEmail(resetPasswordToken.getEmail());
            log.info("Token will be duplicated!");
        }


        User userData = userRepository.findByEmail(resetPasswordEmail);
        ResetPasswordToken passwordToken = new ResetPasswordToken();
        passwordToken.setEmail(userData.getEmail());
        passwordToken.setUsed(false);
        passwordToken.setExpiryDate(30);
        passwordToken.setToken(UUID.randomUUID().toString());
        tokenRepository.save(passwordToken);

        MailRequest mail = new MailRequest();
        mail.setFrom("librarianpanther@gmail.com");
        mail.setTo(userData.getEmail());
        mail.setSubject("Password reset request");

        Map<String, Object> model = new HashMap<>();
        model.put("token", passwordToken);
        model.put("user", userData);
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.put("resetUrl", url + "/reset-password?token=" + passwordToken.getToken());
        mail.setModel(model);
        emailService.sendEmail(mail);

        log.info("Token Generated successfully");
        return ResponseEntity.ok().body(passwordToken);
    }


    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping(value = "/addNewProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingProductRequest> addNewProduct(@RequestBody ProductRequest productRequest) {

        val productName = productRequest.getProductName();
        val file = productRequest.getFile();
        val productAuthor = productRequest.getAuthor();
        val count = productRequest.getNumberInStock();
        val category = productRequest.getCategory();
        val type = productRequest.getBookType();


        if (productRepository.existsByProductName(productName)) {
            log.error("Product with name {} already exists", productName);
            val product = new ExistingProductRequest(null);
            return ResponseEntity.badRequest().body(product);
        }

        log.info("Creating new Product...");
        Product newProduct = new Product();
        newProduct.setId(UUID.randomUUID().toString());
        newProduct.setProductName(productName);
        newProduct.setAuthor(productAuthor);
        newProduct.setNumberInStock(count);
        newProduct.setToOrder(1);
        newProduct.setCount(1);
        newProduct.setCategory(category);
        newProduct.setBookType(type);
        newProduct.setFile(file);
        val product = new ExistingProductRequest(newProduct);
        productRepository.save(newProduct);
        return ResponseEntity.ok().body(product);

    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping(value = "/makeOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object makeNewOrder(@RequestBody OrderRequestSchema orderRequest) {

        //log.info("1");
        log.info("Creating new Order!");
        val email = orderRequest.getEmail();
        val orderedItems = orderRequest.getOrderedItems();
       // log.info("2");
        if (!userRepository.existsByEmail(email)) {
            val order = new OrderRequest(null);
          //   log.info("3");
            return ResponseEntity.badRequest().body(order);
        }
       // log.info("4");
        JSONArray ord = new JSONArray(orderedItems);
        List<Integer> myList = new ArrayList<>();
        List<StockProductCount> productsToCheck = new ArrayList<>();
      //  log.info("5");

        int orderCount;
        String itemId;
      //  log.info("6");
        for (int i = 0; i < ord.length(); i++) {
            try {
                orderCount = orderedItems.get(i).getToOrder();
                itemId = orderedItems.get(i).getId();
                StockProductCount productCountToCheck = new StockProductCount();
                productCountToCheck.setId(itemId);
                productCountToCheck.setCount(orderCount);
                productsToCheck.add(productCountToCheck);
                       //log.info("7");
                myList.add(orderCount);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } //log.info("8");
        int sum = 0;
        for (int num : myList) {

            sum += num;
        }
       // log.info("9");
        // Calculating a stock from database

        for (StockProductCount stockProductCount : productsToCheck) {
            val idOfProductFormCheckList = stockProductCount.getId();
            Product product = productRepository.findProductById(idOfProductFormCheckList);

            //  log.info("10");
            if (!productRepository.existsById(idOfProductFormCheckList)) {
                val order = new OrderRequest(null);
                return ResponseEntity.badRequest().body(order);
            }
           //  log.info("11");
            if (product.getNumberInStock() < stockProductCount.getCount()) {
                // log.info("782839283289328938923892839829389238");
                val order = new OrderRequest(null);
                return ResponseEntity.badRequest().body(order);

            }
        }
       // log.info("12");
        for (StockProductCount stockProductCount : productsToCheck) {
            val idOfProductFormCheckList = stockProductCount.getId();
            Product product = productRepository.findProductById(idOfProductFormCheckList);
           //   log.info("13");
            productRepository.findById(idOfProductFormCheckList)
                    .map(updatedProduct -> {
                        val newStock = product.getNumberInStock() - stockProductCount.getCount();
                        updatedProduct.setNumberInStock(newStock);
                        System.out.println(updatedProduct.getNumberInStock());
                        return productRepository.save(updatedProduct);
                    });
        }
        // log.info("14");
        User userData = userRepository.findByEmail(email);
        val id = userData.getId();
        int finalOrderCount = sum;
        userRepository.findById(id)
                .map(NewUser -> {
                    NewUser.setBookedNow(userData.getBookedNow() + finalOrderCount);
                    return userRepository.save(NewUser);
                });
        Order newOrder = new Order();
        newOrder.setId(UUID.randomUUID().toString());
        newOrder.setEmail(email);
        newOrder.setOrderedItems(orderedItems);
       //  log.info("15");
        log.info("Order created");
       // log.info("16");
        orderRepository.save(newOrder);
        log.info("Order saved in database");

        //log.info("17");
        val order = new OrderRequest(newOrder);
        return ResponseEntity.ok().body(order);


    }
    @SneakyThrows
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @DeleteMapping(value = "/deleteOrder/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {

        if (!orderRepository.existsById(id)) {
            log.error("Sorry but order with id: {}", id);
            val errorMessage = String.format("Sorry but order with id: %s", id);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        Order order = orderRepository.findOrderById(id);
        val email = order.getEmail();
        val orderedItems = order.getOrderedItems();
        JSONArray ord = new JSONArray(orderedItems);
        List<Integer> myList = new ArrayList<>();

        int orderCount;
        // log.info("6");
        for (int i = 0; i < ord.length(); i++) {
            try {
                orderCount = orderedItems.get(i).getToOrder();
                //       log.info("7");
                myList.add(orderCount);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } // log.info("8");
        int sum = 0;
        for (int num : myList) {

            sum += num;
        }
        int finalOrderCount = sum;

        User userData = userRepository.findByEmail(email);
        val userId = userData.getId();
        userRepository.findById(userId)
                .map(newUser -> {
                    newUser.setRead(userData.getRead() + finalOrderCount);
                    newUser.setBookedNow(userData.getBookedNow() - finalOrderCount);
                    return userRepository.save(newUser);
                });
        System.out.println(orderedItems);
        for (OrderSchema product: orderedItems) {

            if (!productRepository.existsById(product.getId())) throw new Exception ("Product not Exists");


                productRepository.findById(product.getId())
                        .map(newProduct -> {
                            newProduct.setNumberInStock(product.getToOrder());
                            return productRepository.save(newProduct);
                        });


        }

        orderRepository.deleteById(id);
        log.info("Order with id: {} has been deleted", id);
        val successMessage = String.format("Order with id: %s has been deleted", id);
        return ResponseEntity.ok().body(successMessage);
    }
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @PutMapping(value = "/updateProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingProductRequest> updateProduct(@RequestBody UpdateProductRequest updateProductRequest) {


        val id = updateProductRequest.getId();
        val newNumberInStock = updateProductRequest.getNumberInStock();

        log.info("Updating product with id: {}", id);
        if (!productRepository.existsById(id)) {
            val product = new ExistingProductRequest(null);
            log.error("Product doesn't exists");
            return ResponseEntity.badRequest().body(product);
        }

        productRepository.findById(id)
                .map(product -> {
                    product.setNumberInStock(newNumberInStock);
                    log.info("stock updated to: {}", newNumberInStock);
                    return productRepository.save(product);
                });


        Product product = productRepository.findProductById(id);

        val newProduct = new ExistingProductRequest(product);

        return ResponseEntity.ok().body(newProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        log.info("Deleting Product with id: {}", id);

        if (!productRepository.existsById(id)) {
            log.error("Product with id: {} doesn't exists", id);
            val errorMessage = String.format("Sorry but product with id %s doesn't exists", id);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        productRepository.deleteById(id);
        log.info("Product with id: {} has been deleted", id);
        val successMessage = String.format("Product with id: %s has been deleted", id);
        return ResponseEntity.ok().body(successMessage);
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/getAllOrders/{email}")
    public ResponseEntity<List<Order>> findAllOrders(@PathVariable String email) {

        if (!userRepository.existsByEmail(email)) {
            log.error("User with email: {} doesn't exists", email);
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(orderRepository.findAllByEmail(email));
    }


    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> findAllProducts() {
        log.info("finding all Products");
        return ResponseEntity.ok().body(productRepository.findAll());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> findAllUsers() {
        log.info("Trying to find all users");
        return ResponseEntity.ok().body(userRepository.findAll());
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/findUserByEmail/{email}")
    public ResponseEntity<User> findUser(@PathVariable String email) {
        log.info("Finding user with email {}", email);
        return ResponseEntity.ok().body(userRepository.findByEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUserByEmial/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        log.info("Deleting user {}", email);

        if (!userRepository.existsByEmail(email)) {
            log.error("User with email {} doesn't exist in the DB. User has't been deleted", email);
            val errorMessage = String.format("Sorry but user wirh email %s doesn't exists", email);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        log.info("User with email {} has been deleted", email);
        val successMessage = String.format("User wirh email %s was deleted", email);
        return ResponseEntity.ok().body(successMessage);

    }
}
