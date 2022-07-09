package com.mysite.sbb.controller;

import com.mysite.sbb.Ut.Ut;
import com.mysite.sbb.dao.ArticleRepository;
import com.mysite.sbb.dao.UserRepository;
import com.mysite.sbb.domain.Article;
import com.mysite.sbb.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usr/article")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/test")
    @ResponseBody
    public String testFunc() {
        return "test11";
    }


    @RequestMapping("/list")
    public String showList(Model model) {
        List<Article> articles = articleRepository.findAll();

        model.addAttribute("articles",articles);

        return "usr/article/list";
    }

    @RequestMapping("/list2")
    @ResponseBody
    public List<Article> showList2() {
        return articleRepository.findAll();
    }

    @RequestMapping("/get")
    @ResponseBody
    public Article getArticle(Long id) {
        Article article = articleRepository.findById(id).get();

        return article;
    }

    @RequestMapping("/modify")
    public String showModify(Long id, Model model) {
        Optional<Article> opArticle = articleRepository.findById(id);
        Article article = opArticle.get();

        model.addAttribute("article", article);

        return "usr/article/modify";
    }

    @RequestMapping("/doModify")
    @ResponseBody
    public String doModify(Long id, String title, String body) {
        if (id == null) {
            return "id를 입력하세요.";
        }

        if (title == null) {
            return "title을 입력하세요.";
        }

        if (body == null) {
            return "body를 입력하세요.";
        }

        Article article = articleRepository.findById(id).get();

        article.setUpdateDate(LocalDateTime.now());

        article.setTitle(title);
        article.setBody(body);

        articleRepository.save(article);

        return """
                <script>
                alert("%d번 게시물을 수정했습니다.");
                location.replace('detail?id=%d')
                </script>
                """.formatted(article.getId(),article.getId());
    }

    @RequestMapping("/doDelete")
    @ResponseBody
    public String doDelete(Long id, HttpSession httpSession) {
        boolean islogined = false;
        long loginedUserId = 0;

        if(httpSession.getAttribute("loginedUserId")!= null){
            islogined = true;
            loginedUserId = (long)httpSession.getAttribute("loginedUserId");
        }

        if(!islogined){
            return """
                <script>
                alert("로그인 해주세요.");
                history.back();
                </script>
                """;
        }


        if (!articleRepository.existsById(id)) {
            return """
                <script>
                alert("%d번 게시물은 이미 삭제되었습니다.");
                history.back();
                </script>
                """.formatted(id);
        }

        Article article = articleRepository.findById(id).get();

        if(article.getUser().getId() != loginedUserId){
            return """
                <script>
                alert("권한이 없습니다.");
                history.back();
                </script>
                """;
        }


        articleRepository.delete(article);

        return """
                <script>
                alert("%d번 게시물이 삭제되었습니다.");
                location.replace('list')
                </script>
                """.formatted(id);
    }

    @RequestMapping("/detail")
    public String showDetail(Long id, Model model) {
       Optional<Article> opArticle = articleRepository.findById(id);
       Article article = opArticle.get();

       model.addAttribute("article", article);

       return "usr/article/detail";
    }

    @RequestMapping("/write")

    public String doWrite(HttpSession httpSession, Model model){
        boolean islogined = false;
        long loginedUserId = 0;

        if(httpSession.getAttribute("loginedUserId")!= null){
            islogined = true;
            loginedUserId = (long)httpSession.getAttribute("loginedUserId");
        }

        System.out.println("islogined: " + islogined);

        if(!islogined){
            model.addAttribute("msg","로그인 후 이용해주세요.");
            model.addAttribute("historyBack",true);
            return "common/js";
        }
        return "usr/article/write";
    }

    @RequestMapping("/doWrite")
    @ResponseBody
    public String doWrite(String title, String body, HttpSession httpSession){
        boolean islogined = false;
        long loginedUserId = 0;

        if(httpSession.getAttribute("loginedUserId")!= null){
            islogined = true;
            loginedUserId = (long)httpSession.getAttribute("loginedUserId");
        }

        if(!islogined){
            return """
                <script>
                alert("로그인 해주세요.");
                history.back();
                </script>
                """;
        }

        if(Ut.empty(title)){
            return "제목을 입력해주세요";
        }
        if(Ut.empty(body)){
            return "내용을 입력해주세요";
        }

        body = body.trim();

        Article article = new Article();
        article.setRegDate(LocalDateTime.now());
        article.setUpdateDate(LocalDateTime.now());
        article.setTitle(title);
        article.setBody(body);
        User user = userRepository.findById(loginedUserId).get();
        article.setUser(user);

        articleRepository.save(article);

        return """
                <script>
                alert("%d번 게시물이 생성되었습니다.");
                location.replace('list')
                </script>
                """.formatted(article.getId());
    }

}
