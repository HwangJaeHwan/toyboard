package com.example.board.toyboard.Service;


import com.example.board.toyboard.Entity.Comment;
import com.example.board.toyboard.Entity.Report.CommentReport;
import com.example.board.toyboard.Entity.Report.Report;
import com.example.board.toyboard.Entity.User;
import com.example.board.toyboard.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReportService {


    private final ReportRepository reportRepository;


    public void commentReport(User user, Comment comment) {


        Report report = new CommentReport(user, comment);

        reportRepository.save(report);


    }


    @Transactional(readOnly = true)
    public boolean commentReportCheck(User user, Comment comment){

        if (reportRepository.findByUserAndComment(user, comment).isPresent()) {
            return false;
        }

        return true;

    }



}
