/**
 * projectName: mh
 * fileName: FightInfo.java
 * packageName: com.chenjian.entity
 * date: 2019-09-30 16:28
 * copyright(c) 2019 http://www.hydee.cn/ Inc. All rights reserved.
 */
package com.chenjian.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author: ChenJian
 * @description:
 * @data: 2019-09-30 16:28
 **/
@Data
@Table(name = "fight_info")
public class FightInfo {

//  `id` int(255) NOT NULL AUTO_INCREMENT,
//  `hunter_id` bigint(200) DEFAULT NULL,
//  `monster_id` bigint(200) DEFAULT NULL,
//  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL,
//  `create_time` timestamp NULL DEFAULT NULL,

    @Column(name = "id")
    private Long id;

    @Column(name = "hunter_id")
    private Long hunterId;

    @Column(name = "monster_id")
    private Long monsterId;

    @Column(name = "remark")
    private String remark;

    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Column(name = "create_time")
    private Timestamp createTime;

    public FightInfo(){}

    public FightInfo(Long hunterId, Long monsterId, String remark, Timestamp createTime){

        this.hunterId = hunterId;
        this.monsterId = monsterId;
        this.remark = remark;
        this.createTime = createTime;
        if(createTime == null){
            this.createTime = new Timestamp(System.currentTimeMillis());
        }


    }

}