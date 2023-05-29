package com.testframe.autotest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
class AutoTestApplicationTests {

    @Value("copy.switch")
    private Boolean copySwitch;

    @Test
    void contextLoads() {
        System.out.println(copySwitch);
    }
    void contextLoads2() {
        List<Person> list = new ArrayList<Person>() {{
            add(new Person(1, "北京"));
            add(new Person(2, "西安"));
            add(new Person(3, "上海"));
        }};
        List<Integer> sorts = new ArrayList<Integer>() {{
            add(3);
            add(1);
            add(2);
        }};
        Collections.sort(list, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return sorts.indexOf(p1.getAge()) - sorts.indexOf(p2.getAge());
            }
        });
        System.out.println(1);
        System.out.println(list);
    }

}
