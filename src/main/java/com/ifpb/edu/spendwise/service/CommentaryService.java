package com.ifpb.edu.spendwise.service;

import java.time.LocalDate;
// import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.model.Commentary;
import com.ifpb.edu.spendwise.model.Transaction;
import com.ifpb.edu.spendwise.repository.CommentaryRepository;
import com.ifpb.edu.spendwise.repository.TransactionRepository;

@Service
public class CommentaryService {

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public void updateComment(Long commentId, String newText) {
        Commentary comment = commentaryRepository.findById(commentId).orElseThrow();
        comment.setText(newText);
        comment.setDateLastUpdate(LocalDate.now());
        commentaryRepository.save(comment);
    }

    public Commentary addComment(Long transactionId, String text) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow();

        Commentary comment = new Commentary(null, text, LocalDate.now(), LocalDate.now(), transaction);
        transaction.addCommentary(comment);

        return commentaryRepository.save(comment);
    }

    public Commentary editComment(Long commentId, String newText) {
        Commentary comment = commentaryRepository.findById(commentId).orElseThrow();
        comment.setText(newText);
        return commentaryRepository.save(comment);
    }

    public Page<Commentary> getCommentariesByTransaction(Long transactionId, Pageable pageable) {
        return commentaryRepository.findByTransactionId(transactionId, pageable);
    }

    public void deleteComment(Long commentId) {
        commentaryRepository.deleteById(commentId);
    }

    public boolean hasComments(Long transationId) {
        return commentaryRepository.existsByTransactionId(transationId);
    }

    public int countComments(Long transationId) {
        return commentaryRepository.countByTransactionId(transationId);
    }

    public List<Commentary> listCommentsByTransaction(Long transactionId) {
        return commentaryRepository.findByTransactionId(transactionId);
    }

    public Commentary findById(Long commentId) {
        return commentaryRepository.findById(commentId).orElseThrow();
    }
}
