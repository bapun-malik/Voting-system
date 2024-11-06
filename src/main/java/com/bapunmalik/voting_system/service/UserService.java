package com.bapunmalik.voting_system.service;





import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bapunmalik.voting_system.models.TempUser;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;


    public User saveUser(User user) {
        return userRepository.save(user);
    }


    
    public void saveTempUser(User user, MultipartFile photo, MultipartFile signature) {
        session.setAttribute("tempUser", user);
        session.setAttribute("tempPhoto", photo);
        session.setAttribute("tempSignature", signature);
    }


    public User getTempUser() {
        return (User) session.getAttribute("tempUser");
    }

    public byte[] getTempPhotoBytes() {
        return (byte[]) session.getAttribute("tempPhotoBytes");
    }

    public byte[] getTempSignatureBytes() {
        return (byte[]) session.getAttribute("tempSignatureBytes");
    }

    public void clearTempUser() {
        session.removeAttribute("tempUser");
        session.removeAttribute("tempPhoto");
        session.removeAttribute("tempSignature");
    }

}
