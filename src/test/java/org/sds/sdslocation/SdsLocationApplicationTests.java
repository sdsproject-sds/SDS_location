package org.sds.sdslocation;

import org.junit.jupiter.api.Test;
import org.sds.sdslocation.service.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SdsLocationApplicationTests {

    @Autowired
    private LocationServiceImpl locationService;

    @Test
    void contextLoads() {
    }

    @Test
    void  regionsFetch(){
       // locationService.getRegion("36.8888","-1.2199");
    }

}
