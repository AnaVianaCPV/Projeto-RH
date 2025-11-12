package com.RHgroup.CadastrosRH;


import com.rhgroup.cadastrosrh.CadastrosRhApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = CadastrosRhApplication.class)
@ActiveProfiles("test")
class CadastrosRhApplicationTests {

    @Test
    void contextLoads() {
    }
}