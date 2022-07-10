package com.mysite.sbb.controller;

import com.mysite.sbb.Ut.Ut;
import com.mysite.sbb.dao.UserRepository;
import com.mysite.sbb.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/usr/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/join")
    public String showJoin(){
        return "usr/user/join";
    }

    @RequestMapping("/doJoin")
    @ResponseBody
    public String doJoin(String name, String email, String password) {

        name = name.trim();

        if (Ut.empty(name)) {
            return """
                    <script>
                    alert("이름을 입력해주세요.");
                    history.back();
                    </script>
                    """;
        }

        email = email.trim();

        if (Ut.empty(email)) {
            return """
                    <script>
                    alert("이메일을 입력해주세요.");
                    history.back();
                    </script>
                    """;
        }

        boolean existsByEmail = userRepository.existsByEmail(email);

        if (existsByEmail) {
            return """
                    <script>
                    alert("입력하신 이메일(%s)은 이미 사용중입니다.");
                    history.back();
                    </script>
                    """.formatted(email);
        }

        password = password.trim();

        if (Ut.empty(password)) {
            return """
                    <script>
                    alert("비밀번호를 입력해주세요.");
                    history.back();
                    </script>
                    """;
        }

        User user = new User();
        user.setRegDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        return """
                    <script>
                    alert("%s님 회원가입이 완료되었습니다.");
                    location.replace("/");
                    </script>
                    """.formatted(user.getName());
    }

    @RequestMapping("/login")
    public String showLogin(HttpSession session, Model model) {
        boolean islogined = false;
        long loginedUserId = 0;

        if (session.getAttribute("loginedUserId") != null) {
            islogined = true;
            loginedUserId = (long)

                    session.getAttribute("loginedUserId");
        }

        System.out.println("islogined: " + islogined);

        if (islogined) {
            model.addAttribute("msg", "이미 로그인 되어있습니다.");
            model.addAttribute("historyBack", true);
            return "common/js";
        }
        return "usr/user/login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public String doLogin(String email, String password, HttpServletRequest req, HttpServletResponse res) {

        if (email == null || email.trim().length() == 0) {
            return """
                    <script>
                    alert("이메일을 입력해주세요.");
                    history.back();
                    </script>
                    """;
        }

        email = email.trim();

//        User user = userRepository.findByEmail(email).orElse(null); 방법1
        Optional<User> user = userRepository.findByEmail(email); //방법2

        if (user.isEmpty()) {
            return """
                    <script>
                    alert("일치하는 회원이 존재하지 않습니다.");
                    history.back();
                    </script>
                    """;
        }

        if (password == null || password.trim().length() == 0) {
            return """
                    <script>
                    alert("비밀번호를 입력해주세요.");
                    history.back();
                    </script>
                    """;
        }

        password = password.trim();

        if (user.get().getPassword().equals(password) == false) {
            return """
                    <script>
                    alert("비밀번호가 일치하지 않습니다.");
                    history.back();
                    </script>
                    """;
        }

        HttpSession session = req.getSession();
        session.setAttribute("loginedUserId", user.get().getId());
        // key : value
        return """
                    <script>
                    alert("%s님 환영합니다.");
                    location.replace('/')
                    </script>
                    """.formatted(user.get().getName());
    }

    @RequestMapping("/me")
    @ResponseBody
    public User showMe(HttpSession session) {
        boolean isLogined = false;
        long loginedUserId = 0;

        if (session.getAttribute("loginedUserId") != null){
            isLogined = true;
            loginedUserId = (long)session.getAttribute("loginedUserId");
        }

        if(isLogined == false){
            return null;
        }

        Optional<User> user = userRepository.findById(loginedUserId);

        if(user.isEmpty()){
            return null;
        }

        return user.get();
    }

    @RequestMapping("/doLogout")
    @ResponseBody
    public String doLogout(HttpSession session) {
        boolean isLogined = false;

        if(session.getAttribute("loginedUserId") != null){
            isLogined = true;
        }

        if(isLogined == false){
            return """
                    <script>
                    alert("이미 로그아웃 되었습니다.");
                    history.back();
                    </script>
                    """;
        }

        session.removeAttribute("loginedUserId");

        return """
                    <script>
                    alert("로그아웃 되었습니다.");
                    location.replace('/');
                    </script>
                    """;
    }


}
