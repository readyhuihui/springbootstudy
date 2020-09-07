package com.yonghui.springbootjedis.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @Author:lyh
 * @Description:测试连接
 * @Date:Created in 2020/9/1 14:24
 */
public class TestJedis {

    @Test
    public void testJedis(){
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");
        jedis.set("Jedis", "Hello Work!");
        System.out.println(jedis.get("Jedis"));
        jedis.del("Jedis");
        System.out.println(jedis.get("Jedis"));
        jedis.close();

    }

    @Test
    public void testList(){
        List<String> array = new ArrayList<>();
        array.add("a");
        array.add("b");
        array.add("c");
        array.add("a");
        Map<String,Object> map = new HashMap<>();
        map.put("mediaIds",array);
        List<String> list = (List<String>) map.get("mediaIds");
        Set<String> set = new HashSet<>(list);
        set.add("f");
        String str="{_id=5f50ac1b92689c0006515146, readStatus=0, cDetail={reporters=[], departTime=2020-09-03 16:40:00, clueDate=2020-09-03 16:40:00, source=PC创建, contentHtml=, content=, relatedPerson=, specialNeed=, contact=, contentLength=0, startTime=2020-09-03 16:40:00, appIdChannel=, place=, interviewPlace=, contactMethod=, title=浪人情歌1, StationDispatchStatus=0, yddStartTime=2020-09-03 16:40:00, assignTime=2020-09-03 16:41:00, assignId=A0CA5BEBA6ED4EAE943CCD58DC742F9F, assignName=mtyzh002, assignReporter=[{departmentName=cs, companyId=4C423C5621614DAA, loginId=mtyzh002, headImgUrl=https://alpha-obs.yunshicloud.com/4C423C5621614DAA/YDD_YUNSHI/A0CA5BEBA6ED4EAE943CCD58DC742F9F/09709C3E6E4543629C1415B0CFB038DE.jpg, roleId=89CEAE1A230C48D9AD21A332A2CEBEA3, userPhone=18777778881, departmentId=5iY0YhG3IMIQ7KUvoEgMJ7yzdjURL4sq, roleName=管理员角色, userName=mtyzh002, userId=A0CA5BEBA6ED4EAE943CCD58DC742F9F, execution=1, personClaimStatus=待认领, userIdPersonClaimStatus=A0CA5BEBA6ED4EAE943CCD58DC742F9F待认领, claimUpdateTime=2020-09-03 16:41:00}], cameraPerson=[], editPersion=[], driver=[], assignIds=[{userIdPersonClaimStatus=A0CA5BEBA6ED4EAE943CCD58DC742F9F待认领, personClaimStatus=待认领, id=A0CA5BEBA6ED4EAE943CCD58DC742F9F}], asignStatus=1, claimStatus=待认领, taskStatus=已变更, mainMids=[5f4608f6b5af850006bf2250, 5f505f6392689c0006514fc1], PersonMids=[{personMid=5f460bacb5af850006bf225d, personCtime=2020-09-03 16:41:28, personUserName=mtyzh002, personUserId=A0CA5BEBA6ED4EAE943CCD58DC742F9F}, {personMid=5f460c32b5af850006bf225f, personCtime=2020-09-03 16:41:28, personUserName=mtyzh002, personUserId=A0CA5BEBA6ED4EAE943CCD58DC742F9F}, {personMid=5f460b62b5af850006bf225b, personCtime=2020-09-03 16:41:28, personUserName=mtyzh002, personUserId=A0CA5BEBA6ED4EAE943CCD58DC742F9F}]}, companyName=测试租户008, departmentId=5iY0YhG3IMIQ7KUvoEgMJ7yzdjURL4sq, cuserName=mtyzh002, uuserName=mtyzh002, statusDetail=处理成功, appCode=YDD_YUNSHI, title=浪人情歌1, templateId=5d118c9b7ff8587519d16b74, extended={cType=cTypeTitle, extend1=, mediaIds=[5f505f6392689c0006514fc1, 5f4608f6b5af850006bf2250], belongerName=mtyzh002, cStatus=未送审, belonger=A0CA5BEBA6ED4EAE943CCD58DC742F9F}, ctime=2020-09-03 16:40:59, cuserId=A0CA5BEBA6ED4EAE943CCD58DC742F9F, ctimeStamp=1599122459724, uuserId=A0CA5BEBA6ED4EAE943CCD58DC742F9F, departmentName=cs, src=采访任务, utime=2020-09-03 16:41:28, companyGroup=5B62A6C542FA49A1AD219FD388440751, companyId=4C423C5621614DAA, utimeStamp=1599122488131, grade={name=无, id=}, mIds=[5f50ac1b92689c0006515147, 5f50ac1b92689c0006515148, 5f50ac3892689c0006515149, 5f50ac3892689c000651514a, 5f50ac3892689c000651514b], isDel=0, status=SUCCESS}";
    }


}
