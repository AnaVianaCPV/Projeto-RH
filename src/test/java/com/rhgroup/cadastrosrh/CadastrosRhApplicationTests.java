
package com.rhgroup.cadastrosrh;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;



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