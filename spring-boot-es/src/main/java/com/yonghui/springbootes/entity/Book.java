package com.yonghui.springbootes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:lyh
 * @Description:
 * @Date:Created in 2020/8/26 20:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
//    private @Id
//    String id;
    private String price;
    private String title;
    private String image;
}
