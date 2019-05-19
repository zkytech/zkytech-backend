package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.bean.params.ArticleParams;
import com.zkytech.zkytech.entity.Article;
import com.zkytech.zkytech.entity.Classification;
import com.zkytech.zkytech.repository.ArticleRepository;
import com.zkytech.zkytech.repository.ClassificationRepository;
import com.zkytech.zkytech.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
* @author : Zhang Kunyuan
* @date: 2019/5/7 0007 18:47
* @description:
*/
@RestController
@RequestMapping("article")
public class ArticleController {
    private final ArticleRepository articleRepository;
    private final ClassificationRepository classificationRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository, ClassificationRepository classificationRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.classificationRepository = classificationRepository;

        this.commentRepository = commentRepository;
    }

    /**
     * 获取文章列表、搜索、分类
     * @param page :第几页
     * @param pageSize :每一页的大小
     * @param keyword :搜索的关键字
     * @return : {@link MyApiResponse}
     * @author : Zhang Kunyuan
     * @date : 2019/5/3 0003 10:59
     */

    @GetMapping("/list")
    public MyApiResponse getArticleList(@RequestParam int page, @RequestParam int pageSize, @RequestParam(required = false) String keyword, @RequestParam(required = false) Long endId, @RequestParam(required = false) Long classificationId){
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);
        Page articlePage;
        if(endId != null){
            Article endArticle = articleRepository.findArticleById(endId);
            articlePage = articleRepository.findAllByCreatedDateBefore(endArticle.getCreatedDate(), pageable);
        }else{
            if(keyword!=null && !keyword.isEmpty()){
            articlePage = articleRepository.findAllByContentContaining(keyword,pageable);
            }else{
                if(classificationId != null){
                    articlePage = articleRepository.findAllByClassification_Id(classificationId, pageable);
                }else{
                    articlePage = articleRepository.findAll(pageable);
                }
            }
        }
        MyApiResponse<Page<Article>> apiResponse = new MyApiResponse<>();
        apiResponse.setData(articlePage);
        apiResponse.setSuccess(true);
        return  apiResponse;
    }


    /**
     * 添加文章
     * @param params : {title:"title", article:"article", classificationId:""}
     * @return : {@link MyApiResponse}
     * {
     *     "success":true/false
     * }
     * @author : Zhang Kunyuan
     * @date : 2019/5/3 0003 11:04
     */

    @PutMapping
    public MyApiResponse addArticle(@RequestBody ArticleParams params){
        Classification classification = classificationRepository.findClassificationById(params.getClassificationId());
        MyApiResponse apiResponse = new MyApiResponse();
        if (classification == null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("文章分类ID异常（未查询到该ID）");
            return apiResponse;
        }
        Article article = new Article(params.getTitle(), params.getContent(),classification);
        articleRepository.save(article);
        apiResponse.setSuccess(true);
        apiResponse.setMessage("文章已保存");
        return apiResponse;
    }

    /**
     * 删除文章
     * @param id :要删除的文章id
     * @return
     */
    @DeleteMapping("/{id}")
    public MyApiResponse deleteArticle(@PathVariable Long id){
        MyApiResponse apiResponse = new MyApiResponse();
        articleRepository.deleteById(id);
        // 删除文章的同时也要删除评论
        commentRepository.deleteCommentsByArticleId(id);
        apiResponse.setMessage("删除成功");
        return apiResponse;
    }




    /**
     * 获取单个文章
     * @param id :
     * @return : {"success":true/false,"message":"",data:{@link Article}/null}
     * {
     *     "success": true/false,
     *     "data":{@link Article}
     * }
     * @author : Zhang Kunyuan
     * @date : 2019/5/3 0003 11:08
     */
    @GetMapping("/{id}")
    public MyApiResponse getArticle(@PathVariable Long id){
        MyApiResponse<Article> apiResponse = new MyApiResponse<>();
        Article article = articleRepository.findArticleById(id);
        if(article == null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("未查询到该文章（ID不存在）");
        }else{
            apiResponse.setSuccess(true);
            apiResponse.setData(article);
        }
        return apiResponse;
    }

    @GetMapping("/rankList")
    public MyApiResponse<Page<Article>> getArticleRankList(@RequestParam(required = false,defaultValue = "1") int page, @RequestParam(required = false,defaultValue = "10") int pageSize){
        MyApiResponse<Page<Article>> apiResponse = new MyApiResponse<>();
        Sort sort = Sort.by(Sort.Direction.DESC,"clicks");
        Pageable pageable = PageRequest.of(page-1,pageSize,sort);
        Page<Article> articlePage = articleRepository.findArticlesWithoutContent(pageable);
        apiResponse.setData(articlePage);
        return apiResponse;
    }


    /**
     * 修改文章
     * @param params
     * @return
     */
    @PostMapping
    public MyApiResponse editArticle(@RequestBody ArticleParams params){
        MyApiResponse apiResponse = new MyApiResponse();
        Classification classification = classificationRepository.findClassificationById(params.getClassificationId());
        Article article = articleRepository.findArticleById(params.getId());
        article.setClassification(classification);
        article.setContent(params.getContent());
        article.setTitle(params.getTitle());
        articleRepository.save(article);
        apiResponse.setMessage("修改成功");
        return apiResponse;
    }

    /**
     * 增加一次点击
     * @param id
     * @return
     */
    @PostMapping("click/{id}")
    public MyApiResponse addClick(@PathVariable Long id){
        MyApiResponse apiResponse = new MyApiResponse();
        Article article = articleRepository.findArticleById(id);
        article.setClicks(article.getClicks()+1);
        articleRepository.save(article);
        apiResponse.setMessage("id："+id+"点击量+1");
        return apiResponse;
    }
}
