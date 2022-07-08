package com.mysite.sbb.controller;

import com.mysite.sbb.Ut.Ut;
import com.mysite.sbb.dao.UserRepository;
import com.mysite.sbb.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/usr/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/test")
    @ResponseBody
    public String testFunc(){
        return "test";
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<User> showList(){
        return userRepository.findAll();
    }

    @RequestMapping("/doJoin")
    @ResponseBody
    public String doJoin(String name, String email, String password){
        if(Ut.empty(name)){
            return "이름을 입력해주세요";
        }
        if(Ut.empty(email)){
            return "이메일을 입력해주세요";
        }

        if(Ut.empty(password)){
            return "비밀번호를 입력해주세요";
        }

        password = email.trim();

        User user = new User();
        user.setRegDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        return "%d번 회원이 생성되었습니다.".formatted(user.getId());
    }
}
