package spring.security.test.demo.controllers;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.security.test.demo.model.token.ResetPasswordToken;
import spring.security.test.demo.model.user.User;
import spring.security.test.demo.payload.request.forgotPassword.ForgotPasswordDTO;
import spring.security.test.demo.repos.PasswordTokenRepo;
import spring.security.test.demo.repos.UserRepo;
import spring.security.test.demo.security.Argon2PasswordEncoder;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/reset-password")
public class PasswordResetController {


    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }


    @Autowired
    private UserRepo userRepository;
    @Autowired
    private PasswordTokenRepo tokenRepository;

    @ModelAttribute("passwordResetForm")
    public ForgotPasswordDTO passwordReset() {
        return new ForgotPasswordDTO();
    }

    @GetMapping
    public String displayResetPasswordPage(@RequestParam(required = false) String token,
                                           Model model) {

        ResetPasswordToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) {
            model.addAttribute("error", "Token has expired or isn't found please request a new password reset.");
        } else if (resetToken.isExpired()) {
            model.addAttribute("error", "Token has expired or isn't found please request a new password reset.");
        } else {
            model.addAttribute("token", resetToken.getToken());
        }

        return "ResetPasswordPage";
    }

    @PostMapping
    @Transactional
    public String handlePasswordReset(@ModelAttribute("passwordResetForm") @Valid ForgotPasswordDTO form,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ".passwordResetForm", result);
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }
        ResetPasswordToken token = tokenRepository.findByToken(form.getToken());
        User userData = userRepository.findByEmail(token.getEmail());
        String updatedPassword = passwordEncoder().encode(form.getPassword());
        val id = userData.getId();
        userRepository.findById(id)
                .map(NewUser -> {
                    NewUser.setPassword(updatedPassword);
                    return userRepository.save(NewUser);
                });
        tokenRepository.deleteByEmail(token.getEmail());
        return "redirect:/login";

    }


}
