package org.dailycodebuffer.codebufferspringbootmongodb.collection;


import org.dailycodebuffer.codebufferspringbootmongodb.dto.CalculatorDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CollectionTester {

    @Test
    @DisplayName("Tests all DTO's getter setter")
    void testCollection() throws Exception {
        BeanTester beanTester=new BeanTester();
        beanTester.testBean(Person.class);
        //beanTester.testBean(Student.class);
        beanTester.testBean(CalculatorDTO.class);
    }
}
