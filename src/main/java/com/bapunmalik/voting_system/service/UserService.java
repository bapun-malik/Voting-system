package com.bapunmalik.voting_system.service;





import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


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

    public List<User> getAllUser(){
        List<User> users=userRepository.findAll();
        return users;
    }

     public Page<User> getAllUsersPaginated(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public Page<User> getAllUsersPaginatedByApprovalStatus(boolean approvalStatus, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return userRepository.findByApproved(approvalStatus, pageable);
    }

    public long getUserCountByConstituency(String constituency) {
        return userRepository.countByDistrictAndApproved(constituency,true);
    }

    public User getUserById(Long userId) {
        // TODO Auto-generated method stub
        return userRepository.findById(userId).get();
    }
    public void deleteUserById(Long userId) {
        // TODO Auto-generated method stub
        userRepository.deleteById(userId);
    }


}
