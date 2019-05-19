package com.zkytech.zkytech.repository;

import com.zkytech.zkytech.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

//@RepositoryRestResource(exported = true)
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findArticleById(Long id);
    Page<Article> findAllByContentContaining(String keyword, Pageable pageable);
    Page<Article> findAllByCreatedDateBefore(Date date, Pageable pageable);
    Page<Article> findAllByClassification_Id(Long classficationId,Pageable pageable);
    Page<Article> findAllByCreatedDateAfter(Date date, Pageable pageable);

    @Query(value = "select title from Article a where a.id = ?1")
    String findArticleTitleById(Long id);

    @Query(value = "select new Article(ar.id,ar.title, ar.clicks, ar.classification) from Article ar join ar.classification")
    Page<Article> findArticlesWithoutContent(Pageable pageable);

    @Query(value= "select sum(ar.clicks) from Article ar")
    int getTotalClicks();

}
