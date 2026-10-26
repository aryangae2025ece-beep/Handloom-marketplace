package com.handloom.marketplace.config;

import com.handloom.marketplace.exception.BusinessException;
import com.handloom.marketplace.util.Redirects;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Turns expected business errors into a red alert on the page the user came from. */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMsg", ex.getMessage());
        return "redirect:" + target(request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleTooLarge(HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMsg", "That file is too large. Maximum size is 5 MB.");
        return "redirect:" + target(request);
    }

    private String target(HttpServletRequest request) {
        // Never bounce a failed GET back to itself (would loop)
        return "GET".equalsIgnoreCase(request.getMethod()) ? "/" : Redirects.back(request, "/");
    }
}
