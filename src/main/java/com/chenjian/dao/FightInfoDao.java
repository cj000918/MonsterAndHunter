/**
 * projectName: mh
 * fileName: FightInfoDao.java
 * packageName: com.chenjian.dao
 * date: 2019-09-30 16:31
 * copyright(c) 2019 http://www.hydee.cn/ Inc. All rights reserved.
 */
package com.chenjian.dao;

import com.chenjian.entity.base.DBConnection;
import com.chenjian.entity.base.FightInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: ChenJian
 * @description:
 * @data: 2019-09-30 16:31
 **/
@Component
public class FightInfoDao extends DBConnection {

    @Value("${jdbc_username}")
    private String USERNAMR;

    @Value("${jdbc_password}")
    private String PASSWORD ;

    @Value("${jdbc_url}")
    private String URL;


    /**
     * 添加fightInfo
     * @param fightInfo
     * @return
     */
    public long addFightInfo(FightInfo fightInfo){

        int result = -1;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection(USERNAMR, PASSWORD,URL);
            pstmt = con.prepareStatement(" insert into fight_info (" +
                    "hunter_id,monster_id,remark,create_time) " +
                    "values(" +
                    "?,?,?,?)"
            );
            pstmt.setLong(1, fightInfo.getHunterId());
            pstmt.setLong(2, fightInfo.getMonsterId());
            pstmt.setString(3, fightInfo.getRemark());
            pstmt.setTimestamp(4, fightInfo.getCreateTime());

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(con!=null) con.close();
                if(pstmt!=null) pstmt.close();
                if(rs!=null) rs.close();
            }catch (Exception e){

            }
        }
        return result;
    }

    /**
     * 根据hunterId查询
     * @param hunterId
     * @return
     */
    public List<FightInfo> getFightInfoHunterId(Long hunterId, Timestamp time){


        List<FightInfo> fightInfoList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        if(time == null){
            time = new Timestamp(System.currentTimeMillis());
        }

        try {
            con = getConnection(USERNAMR, PASSWORD,URL);
//            pstmt = con.prepareStatement(" select * from  fight_info where hunter_id = ? and create_time > ?");
            pstmt = con.prepareStatement(" select * from  fight_info where hunter_id = ? ");
            pstmt.setLong(1, hunterId);
//            pstmt.setTimestamp(2, time);

            ResultSet  resultSet = pstmt.executeQuery();

            while(resultSet.next()){
                FightInfo fightInfo = new FightInfo();
                fightInfo.setHunterId(resultSet.getLong("hunter_id"));
                fightInfo.setMonsterId(resultSet.getLong("monster_id"));
                fightInfo.setRemark(resultSet.getString("remark"));
                fightInfo.setCreateTime(resultSet.getTimestamp("create_time"));
                fightInfo.setId(resultSet.getLong("id"));
                fightInfoList.add(fightInfo);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(con!=null) con.close();
                if(pstmt!=null) pstmt.close();
                if(rs!=null) rs.close();
            }catch (Exception e){

            }
        }
        return fightInfoList;
    }


}