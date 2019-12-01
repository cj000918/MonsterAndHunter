/**
 * projectName: mh
 * fileName: FightWorker.java
 * packageName: com.chenjian.job
 * date: 2019-09-27 19:25
 * copyright(c) 2019 http://www.hydee.cn/ Inc. All rights reserved.
 */
package com.chenjian.job;

import com.chenjian.entity.HunterNew;
import com.chenjian.entity.MonsterNew;
import com.chenjian.enums.MonsterGradeEnums;
import com.chenjian.others.SpringContextHolder;
import com.chenjian.service.HunterService;
import com.chenjian.service.MonsterService;
import com.chenjian.util.GameUtil;
import com.chenjian.util.JobUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ChenJian
 * @description:
 * @data: 2019-09-27 19:25
 **/

public class FightWorker extends JobWorker{

    private HunterService hunterService = (HunterService) SpringContextHolder.getBean(HunterService.class);

    private MonsterService monsterService = (MonsterService) SpringContextHolder.getBean(MonsterService.class);

    @Override
    public Map<String, String> work(Object data) {

        Map<String, String> returnMap = new HashMap<>();
        HashMap<String, Object> conditionMap = (HashMap<String, Object>) data;
        if(conditionMap == null || conditionMap.size() < 1){

            return returnMap;
        }

        HunterNew hunterNew = (HunterNew) conditionMap.get("hunterNew");
        MonsterNew monsterNew = (MonsterNew) conditionMap.get("monsterNew");

        injured(hunterNew, monsterNew, true);

        return returnMap;
    }



    /**
     * 扣血
     * @param monster
     */
    public void injured(HunterNew hunterNew , MonsterNew monster, Boolean isFirst) {

        String firstCode = null;

        Map<String, Object> fightMap = firstFighting(hunterNew, monster, isFirst);
        firstCode = String.valueOf(fightMap.get("code"));
        HashMap<String,Object> map = new HashMap<>();

        if(firstCode.equals("301")){

            MonsterNew nextMonster = new MonsterNew(hunterNew);
            map.put("hunterNew", hunterNew);
            map.put("monsterNew", nextMonster);
            map.put("isFirst", true);

        }else if (firstCode.equals("300")){

            map.put("hunterNew", hunterNew);
            map.put("monsterNew", monster);
            map.put("isFirst", false);
        }
        JobUtil.post(map, new FightWorker());
    }

    /**
     * 第一次碰面战斗
     * @param hunterNew
     * @param monster
     * @return
     */
    private Map<String, Object> firstFighting(HunterNew hunterNew , MonsterNew monster, Boolean isFirst){

        long lostLife = 0;
        long curLife = 0;
        Map<String, Object> returnMap = new HashMap<>();
        Boolean hiddenResult = GameUtil.hidden(hunterNew.getHideRate(), monster.getHideRate());

        System.out.println(" 遭遇敌人 , " + monster.getTitle());

        //判断谁先手， 如果为flase则monster先手
        if (isFirst == !hiddenResult) {

            System.out.println("【" + hunterNew.getName() + "】" + " 还没反应过来， 就遭到了 【" + monster.getName() + "】的攻击" + "\r\n");
            System.out.println("【"+monster.getTitle()+"】"+" 冲上去咬了 "+"【"+hunterNew.getName()+"】"+"一大口"+"\r\n");

            lostLife = GameUtil.calLostLife(monster.getMaxAttack(), monster.getMinAttack(), hunterNew.getDefend());

            if(lostLife > 0){

                if(monster.getGrade() == MonsterGradeEnums.SHI_XUE.getShowName()){

                    System.out.println("【"+hunterNew.getName()+"】"+":啊啊啊, 巨疼.....嗯? 这怪物还吸血?!!!"+"\r\n");

                    //怪物加血
                    long upLife =  monster.getCurLife() + lostLife;
                    monster.setCurLife(upLife);

                }else{
                    System.out.println("【"+hunterNew.getName()+"】"+":疼疼疼疼疼疼疼疼......"+"\r\n");
                }

                //hunter扣血， 并判断血量是否小于1导致死亡
                curLife = hunterNew.getCurLife() - lostLife;
                hunterNew.setCurLife(curLife);

                System.out.println("【"+hunterNew.getName()+"】"+" 血量: -"+lostLife+"\r\n");

                if(curLife < 1){
                    curLife = 0;
                    hunterService.died(hunterNew);

                    returnMap.put("code", 200);
                    returnMap.put("curLife", curLife);
                    returnMap.put("msg", "hunter死亡， 游戏结束");
                    return returnMap;
                }

                hunterService.showHunterInfo(hunterNew);

            }else{
                System.out.println("【"+hunterNew.getName()+"】"+" 机智的躲过了攻击"+"\r\n");
            }
        }

        System.out.println("【"+hunterNew.getName()+"】"+"面无表情杀向"+"【"+monster.getTitle()+"】");

        //hunter攻击，对monster进行闪避=判断
        if(GameUtil.hidden(monster.getHideRate())){

            System.out.println("【"+monster.getTitle()+"】"+" 躲过了 "+"【"+ hunterNew.getName()+"】"+"的攻击"+"\r\n");

        }else{

            lostLife = GameUtil.calLostLife(hunterNew.getMaxAttack(), hunterNew.getMinAttack(), monster.getDefend());

            curLife = monster.getCurLife() - lostLife;
            monster.setCurLife(curLife);
            System.out.println("【"+monster.getTitle()+"】"+" 受到攻击 "+"\r\n");
            System.out.println("【"+monster.getTitle()+"】"+" 血量: -"+lostLife+"\r\n");

            //monster扣血， 并判断是否死亡
            if(curLife < 1){

                curLife = 0;

                long exp = monsterService.died(monster);

                System.out.println("【"+hunterNew.getName()+"】" + " 增加经验:  "+monster.getMaxLife()+"\r\n");

                hunterService.expAdd(hunterNew, exp);

                returnMap.put("code", 301);
                returnMap.put("curLife", curLife);
                returnMap.put("msg", "monster死亡， 进行下一轮战斗");
                return returnMap;
            }
        }

        System.out.println("【"+monster.getTitle()+"】"+" 冲上去咬了 "+"【"+hunterNew.getName()+"】"+"一大口"+"\r\n");

        //monster进行攻击， 对hunter进行闪避判断
        if(GameUtil.hidden(hunterNew.getHideRate())){

            System.out.println("【"+hunterNew.getName()+"】"+" 机智的躲过了攻击 "+"\r\n");

        }else{

            lostLife = GameUtil.calLostLife(monster.getMaxAttack(), monster.getMinAttack(), hunterNew.getDefend());

            curLife = hunterNew.getCurLife() - lostLife;

            System.out.println("【"+hunterNew.getName()+"】"+" 受到攻击 "+"\r\n");
            System.out.println("【"+hunterNew.getName()+"】"+" 血量: -"+lostLife+"\r\n");
            hunterNew.setCurLife(curLife);
            if(curLife < 1){

                curLife = 0;
                hunterService.died(hunterNew);

                returnMap.put("code", 200);
                returnMap.put("curLife", curLife);
                returnMap.put("msg", "hunter死亡， 游戏结束");
                return returnMap;
            }
            hunterService.showHunterInfo(hunterNew);
        }

        returnMap.put("code", 300);
        returnMap.put("monster", monster);
        returnMap.put("hunter", hunterNew);
        returnMap.put("curLife", curLife);
        returnMap.put("msg", "继续战斗");

        return returnMap;
    }


}