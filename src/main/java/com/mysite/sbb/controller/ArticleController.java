package com.mysite.sbb.controller;

import com.mysite.sbb.dao.ArticleRepository;
import com.mysite.sbb.domain.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/usr/article")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @RequestMapping("/test")
    @ResponseBody
    public String testFunc(){
        return "test";
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<Article> showList(){
        return articleRepository.findAll();
    }

    @RequestMapping("/get")
    @ResponseBody
    public Article getArticle(Long id){
        Article article = articleRepository.findById(id).get();

        return article;
    }

    @RequestMapping("/doModify")
    @ResponseBody
    public String doModify(Long id, String title, String body){
        if(id == null){
            return "id를 입력하세요.";
        }

        if(title == null){
            return "title을 입력하세요.";
        }

        if(body == null) {
            return "body를 입력하세요.";
        }

        Article article = articleRepository.findById(id).get();

        article.setTitle(title);
        article.setBody(body);

        articleRepository.save(article);

        return String.format("%d번 수정이 완료되었습니다.",id);
    }

    @RequestMapping("/doDelete")
    @ResponseBody
    public String doDelete(Long id){
        if(!articleRepository.existsById(id)){
            return "%d번 게시물은 이미 삭제되었거나 존재하지 않습니다.".formatted(id);
        }

        Article article = articleRepository.findById(id).get();

        articleRepository.delete(article);

        return String.format("%d번 삭제가 완료되었습니다.",id);
    }

    @RequestMapping("/doAdd")
    @ResponseBody
    public String doAdd(String title,String body,Long userId){
        if(title == null){
            return "title을 입력하세요.";
        }

        if(body == null) {
            return "body를 입력하세요.";
        }

        if(userId == null) {
            return "userId를 입력하세요.";
        }


        Article article = new Article();
        article.setTitle(title);
        article.setBody(body);

        articleRepository.save(article);

        return String.format("생성되었습니다.");
    }
}
