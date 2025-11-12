
package com.RHgroup.CadastrosRH;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.rhgroup.cadastrosrh.CadastrosRhApplication;


@ActiveProfiles("test")
@SpringBootTest(
        classes = CadastrosRhApplication.class, // GARANTE QUE O CONTEXTO SEJA ENCONTRADO
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class CadastrosRhApplicationTests {

	@Test
	void contextLoads() {
	}
}