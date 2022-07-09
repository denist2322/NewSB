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

import java.time.LocalDateTime;
import java.util.List;

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

        return String.format("%d번 수정이 완료되었습니다.", id);
    }

    @RequestMapping("/doDelete")
    @ResponseBody
    public String doDelete(Long id) {
        if (!articleRepository.existsById(id)) {
            return "%d번 게시물은 이미 삭제되었거나 존재하지 않습니다.".formatted(id);
        }

        Article article = articleRepository.findById(id).get();

        articleRepository.delete(article);

        return String.format("%d번 삭제가 완료되었습니다.", id);
    }
    @RequestMapping("/doWrite")
    @ResponseBody
    public String doWrite(String title, String body){
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
        User user = userRepository.findById(1L).get();
        article.setUser(user);

        articleRepository.save(article);

        return "게시물이 생성되었습니다.";
    }

}
