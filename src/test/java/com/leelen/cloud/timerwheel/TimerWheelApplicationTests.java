package com.leelen.cloud.timerwheel;

import com.leelen.cloud.TimerWheelTest;
import com.leelen.cloud.entity.TestDTO;
import com.leelen.cloud.utils.TimingWheelUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class TimerWheelApplicationTests {
    @Test
    void contextLoads() {
        TestDTO testDTO = new TestDTO("12","xiaowang");

        TimingWheelUtil.taskAdd(testDTO, 10, TimeUnit.SECONDS);
//        Multimap<String,StudentScore> scoreMultimap = ArrayListMultimap.create();
//        for(int i=10;i<20;i++){
//            StudentScore studentScore=new StudentScore();
//            studentScore.CourseId=1001+i;
//            studentScore.score=100-i;
//            scoreMultimap.put("peida",studentScore);
//        }
//        System.out.println("scoreMultimap:"+scoreMultimap.size());
//        System.out.println("scoreMultimap:"+scoreMultimap.entries().size());
//        System.out.println("scoreMultimap:"+scoreMultimap.asMap().entrySet());

//        Collection<StudentScore> studentScore = scoreMultimap.get("peida");
//        StudentScore studentScore1=new StudentScore();
//        studentScore1.CourseId=1034;
//        studentScore1.score=67;
//        studentScore.add(studentScore1);
//
//        StudentScore studentScore2=new StudentScore();
//        studentScore2.CourseId=1045;
//        studentScore2.score=56;
//        scoreMultimap.put("jerry",studentScore2);
//        System.out.println("scoreMultimap:"+scoreMultimap.keys());
//        System.out.println("scoreMultimap:"+scoreMultimap.size());
//        System.out.println("scoreMultimap:"+scoreMultimap.keys());


//        for(StudentScore stuScore : scoreMultimap.values()) {
//            System.out.println("stuScore one:"+stuScore.CourseId+" score:"+stuScore.score);
//        }
//
//        scoreMultimap.remove("jerry",studentScore2);
//        System.out.println("scoreMultimap:"+scoreMultimap.size());
//        System.out.println("scoreMultimap:"+scoreMultimap.get("jerry"));
//
//        scoreMultimap.put("harry",studentScore2);
//        scoreMultimap.removeAll("harry");
//        System.out.println("scoreMultimap:"+scoreMultimap.size());
//        System.out.println("scoreMultimap:"+scoreMultimap.get("harry"));
    }

}
