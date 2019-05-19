package com.zkytech.zkytech.controller;

import com.zkytech.zkytech.Utils;
import com.zkytech.zkytech.bean.MyApiResponse;
import com.zkytech.zkytech.bean.UserType;
import com.zkytech.zkytech.entity.Comment;
import com.zkytech.zkytech.entity.User;
import com.zkytech.zkytech.entity.Vote;
import com.zkytech.zkytech.repository.CommentRepository;
import com.zkytech.zkytech.repository.UserRepository;
import com.zkytech.zkytech.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final Utils utils = new Utils();
    public CommentController(CommentRepository commentRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @DeleteMapping("/{id}")
    public MyApiResponse deleteComment(@PathVariable Long id){
        MyApiResponse apiResponse = new MyApiResponse();
        commentRepository.deleteCommentByid(id);
        apiResponse.setMessage("删除成功");
        return apiResponse;
    }

    @PutMapping()
    public MyApiResponse addComment(@RequestBody Comment comment, Authentication authentication) {
        MyApiResponse apiResponse = new MyApiResponse();

        if (authentication == null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("请在登录后评论");
            return apiResponse;
        }
        User user = (User) authentication.getPrincipal();
        // 统计楼层数
        int floor = comment.getFloor();
        int insideFloor = -1;
        if(comment.getTargetId() == -1){
            Long adminUserId = userRepository.findAdminUserId();
            comment.setTargetUserId(adminUserId);
            floor = commentRepository.countCommentsByArticleIdAndAndTargetId(comment.getArticleId(), -1L) + 1;
        }else{
            insideFloor = commentRepository.countCommentsByArticleIdAndFloor(comment.getArticleId(),floor);
        }
        Comment comment1 = Comment.of(comment.getArticleId(), user.getId(), comment.getTargetUserId(), comment.getContent(), floor, insideFloor, comment.getTargetId(),comment.getTargetFloor());
        commentRepository.save(comment1);
        apiResponse.setMessage("评论成功");
        return apiResponse;
    }


    @GetMapping("/list")
    public MyApiResponse<List<Comment>> getCommentList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "") Long articleId,
            @RequestParam(required = false, defaultValue = "") Long userId,
            @RequestParam(required = false, defaultValue = "false") Boolean reply,
            Authentication authentication
                                                       ) {
        MyApiResponse apiResponse = new MyApiResponse();
        List<Sort.Order> orders=new ArrayList<>();
        Page comments = null;
        if (articleId!=null){
            orders.add(new Sort.Order(Sort.Direction.ASC,"floor"));
            orders.add(new Sort.Order(Sort.Direction.ASC,"insideFloor" ));
            apiResponse.setData(commentRepository.findAllByArticleId(articleId, Sort.by(orders)));
        }else{
            if(userId!=null){
                orders.add(new Sort.Order(Sort.Direction.DESC,"createdDate"));
                if(reply){
                    // 收到的回复
                    comments = commentRepository.findAllByTargetUserId(userId,PageRequest.of(page-1,pageSize,Sort.by(orders)));
                } else{
                    // 自己的评论
                    comments = commentRepository.findAllByUserId(userId,PageRequest.of(page-1,pageSize,Sort.by(orders)));
                }
            }
            else {
                    comments = commentRepository.findAll(PageRequest.of(page-1,pageSize,Sort.by(orders)));
            }
            apiResponse.setData(comments);
        }
        return apiResponse;
    }

    @PutMapping("vote")
    public MyApiResponse vote(@RequestBody Vote vote, Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();
        if(authentication == null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("请在登录后进行操作");
            return apiResponse;
        }
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Long commentId = vote.getCommentId();
        Boolean upvote = vote.isUpvote();
        Vote vote1 = voteRepository.findVoteByCommentIdAndAndUserId(commentId,userId);
        Comment comment = commentRepository.findCommentById(commentId);
        if(vote1 == null){
            // 以前从未点过支持/反对
            // comment表与vote表保持同步更新
            if(upvote){
                comment.setUpvote(comment.getUpvote()+1);
                apiResponse.setMessage("支持成功");
                apiResponse.setData("up");
            }else{
                comment.setDownvote(comment.getDownvote()+1);
                apiResponse.setMessage("反对成功");
                apiResponse.setData("down");

            }
            voteRepository.save(Vote.of(userId,commentId,upvote));
        }else{
            if(vote1.isUpvote()==upvote){
                // 已经点过支持/反对，再次点击就是取消
                if(upvote){
                    comment.setUpvote(comment.getUpvote()-1);
                    apiResponse.setData("deleteUp");
                }else{
                    comment.setDownvote(comment.getDownvote()-1);
                    apiResponse.setData("deleteDown");
                }
                voteRepository.delete(vote1);
                apiResponse.setMessage("取消成功");
            }else{
                vote1.setUpvote(upvote);
                voteRepository.save(vote1);
                if(upvote){
                    // 从反对变成支持
                    comment.setUpvote(comment.getUpvote()+1);
                    comment.setDownvote(comment.getDownvote()-1);
                    apiResponse.setMessage("支持成功");
                    apiResponse.setData("downToUp");
                }else{
                    // 从支持变成反对
                    comment.setUpvote(comment.getUpvote()-1);
                    comment.setDownvote(comment.getDownvote()+1);
                    apiResponse.setMessage("反对成功");
                    apiResponse.setData("upToDown");

                }
            }
        }
        commentRepository.save(comment);
        return apiResponse;
    }

    @GetMapping("/messageCount")
    public MyApiResponse getMessageCount(Authentication authentication){

        MyApiResponse apiResponse = new MyApiResponse();
        if(authentication == null){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("用户未登录");
            return apiResponse;
        }
        User user = (User) authentication.getPrincipal();
        int messageCount = commentRepository.countCommentsByTargetUserIdAndReadIsFalse(user.getId());
        apiResponse.setData(messageCount);
        return apiResponse;
    }

    /**
     * 用户读取信息后，将Message表中对应的read标记为true
     * @param authentication
     * @return
     */
    @PostMapping("/readMessage")
    public MyApiResponse readMessage(Authentication authentication){
        MyApiResponse apiResponse = new MyApiResponse();
        User user = (User) authentication.getPrincipal();
        List<Comment> commentList = commentRepository.findCommentsByTargetUserIdAndReadIsFalse(user.getId());
        Iterator<Comment> commentIterator = commentList.iterator();
        while(commentIterator.hasNext()){
            Comment comment = commentIterator.next();
            comment.setRead(true);
            commentRepository.save(comment);
        }
        return apiResponse;
    }
}
