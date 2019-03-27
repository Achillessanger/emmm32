package com.huawei;

import com.huawei.bean.Car;
import com.huawei.bean.Cross;
import com.huawei.bean.Road;
import com.sun.javafx.collections.MappingChange;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    private static Map<Integer,Car> cars=new HashMap<>();
    private static Map<Integer,Road> roads=new HashMap<>();
    private static Map<Integer,Cross> crosses=new HashMap<>();
    private static List<Integer> notFinishedCarList = new ArrayList<>();
    private static List<Integer> runningCarList = new ArrayList<>();
    private static List<Integer> roadList = new ArrayList<>();
    public static void main(String[] args)
    {
        if (args.length != 4) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");
        readFiles(roadPath,1);
        readFiles(crossPath,2);
        readFiles(carPath,0);

        //init
        for(Map.Entry<Integer,Road> entry : roads.entrySet()){
            roadList.add(entry.getKey());
        }
        roadList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2; //正序
            }
        });
        for(Map.Entry<Integer,Car> entry : cars.entrySet()){
            notFinishedCarList.add(entry.getKey());
        }


        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        writeFile(answerPath);
        logger.info("End...");
    }
    //fileType代表文件类型，0代表车，1代表道路，2代表路口
    public static void readFiles(String path,int fileType) {

        try (FileReader fileReader=new FileReader(path) ){
            BufferedReader br=new BufferedReader(fileReader);
            String line="";
            while ((line=br.readLine())!=null) {
                if(line.charAt(0)=='#') {
                    continue;
                }
                String[] lines=line.split(",");
                int id=0;
                switch (fileType) {
                    case 0:
                        id=Integer.parseInt(lines[0]);
                        cars.put(id,new Car(id,crosses.get(Integer.parseInt(lines[1])),
                                crosses.get(Integer.parseInt(lines[2])),Integer.parseInt(lines[3]),
                                Integer.parseInt(lines[4])));
                        break;
                    case 1:
                        id=Integer.parseInt(lines[0]);
                        roads.put(id,new Road(id,Integer.parseInt(lines[1]),
                                Integer.parseInt(lines[2]),Integer.parseInt(lines[3]),
                                crosses.get(Integer.parseInt(lines[4])),crosses.get(Integer.parseInt(lines[5])),
                                ("1".equals(lines[6]))));
                        break;
                    case 2:
                        id=Integer.parseInt(lines[0]);
                        crosses.put(id,new Cross(id,roads.get(Integer.parseInt(lines[1])),
                                roads.get(Integer.parseInt(lines[2])),roads.get(Integer.parseInt(lines[3])),
                                roads.get(Integer.parseInt(lines[4]))));
                        break;
                    default:
                        break;

                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }

    public static void writeFile(String path) {
        try (FileWriter fileWriter=new FileWriter(path)){
            BufferedWriter bw=new BufferedWriter(fileWriter);
            bw.write("#(carId,StartTime,RoadId……)"+"\r\n");
            String line="";
            for (Integer id:cars.keySet()) {
                line=line+"("+id+","+cars.get(id).getStartTime();
                ArrayList<Road> roads=cars.get(id).getRoute();
                for (Road road:roads) {
                    line=line+","+road.getId();
                }
                line=line+")\r\n";
            }
            bw.write(line);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static int judge(){
        //所有能走的车走掉并变成终止状态，不能走的变成等待状态

        runningCarList.clear();
        int t = 0; //时间片
        while (!areAllCarsGoToTerminals()){
            //当前时间点出发的车上路

        }

        for(/* 按时间片处理 */) {
            while(/* all car in road run into end state */){
                foreach(roads) {
                    /* 调整所有道路上在道路上的车辆，让道路上车辆前进，只要不出路口且可以到达终止状态的车辆
                     * 分别标记出来等待的车辆（要出路口的车辆，或者因为要出路口的车辆阻挡而不能前进的车辆）
                     * 和终止状态的车辆（在该车道内可以经过这一次调度可以行驶其最大可行驶距离的车辆）*/
                    driveAllCarJustOnRoadToEndState(allChannle);/* 对所有车道进行调整 */

                    /* driveAllCarJustOnRoadToEndState该处理内的算法与性能自行考虑 */
                }
            }

            while(/* all car in road run into end state */){
                /* driveAllWaitCar() */
                foreach(crosses){
                    foreach(roads){
                        Direction dir = getDirection();
                        Car car = getCarFromRoad(road, dir);
                        if (conflict){
                            break;
                        }

                        channle = car.getChannel();
                        car.moveToNextRoad();

                        /* driveAllCarJustOnRoadToEndState该处理内的算法与性能自行考虑 */
                        driveAllCarJustOnRoadToEndState(channel);
                    }
                }
            }

            /* 车库中的车辆上路行驶 */
            driveCarInGarage();
        }

    }
    private static boolean areAllCarsGoToTerminals(){
        return (notFinishedCarList.size() == 0);
    }
}