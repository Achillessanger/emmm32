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
//    private static List<Integer> runningCarList = new ArrayList<>();
    private static List<Integer> roadList = new ArrayList<>();
    private static List<Integer> carList = new ArrayList<>();
    private static List<Integer> crossList = new ArrayList<>();

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
        for(Map.Entry<Integer,Car> entry : cars.entrySet()){
            carList.add(entry.getKey());
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
        for(Map.Entry<Integer,Cross> entry : crosses.entrySet()){
            crossList.add(entry.getKey());
        }
        crossList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });



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
                                Integer.parseInt(lines[4]),Integer.parseInt(lines[5]),
                                ("1".equals(lines[6]))));
                        break;
                    case 2:
                        id=Integer.parseInt(lines[0]);
                        crosses.put(id,new Cross(id,Integer.parseInt(lines[1]),
                                Integer.parseInt(lines[2]),Integer.parseInt(lines[3]),
                                Integer.parseInt(lines[4])));
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

//        List<Integer> carsOnRoad = new ArrayList<>();
        List<Car> carsWaitingList = new ArrayList<>();
//        runningCarList.clear();
        int t = 0; //时间片
        while (!areAllCarsGoToTerminals()){


                //先处理每条道路上的车辆，
                // 将这些车辆进行遍历扫描，

            for(Map.Entry<Integer,Road> entry : roads.entrySet()){
                for(int i = 0; i < entry.getValue().getChannel(); i++){ //车道
                    for(int j = entry.getValue().getLength() -1; j >= 0 ; j--){ //在车道上的位置
                        if(entry.getValue().getCarsOnRoad()[i][j] == null)
                            continue;
                        else {
                            Car car = entry.getValue().getCarsOnRoad()[i][j];
                            // 如果车在经过行驶速度（前方没有车辆阻挡）可以出路口，
                            // 将这些车辆标记为等待行驶车辆。
                            if(min(car.getSpeed(),entry.getValue().getSpeed()) > entry.getValue().getLength() - 1 - j) {
                                car.setState(1);
                                carsWaitingList.add(car);
                            }
                                /* b)车辆如果行驶过程中，前方没有阻挡并且也不会出路口（v=min(最大车速，道路限速)），
                                则该车辆行驶可行驶的最大车速（v=min(最大车速，道路限速)），
                                此时该车辆在本次调度确定了该时刻的终止位置。该车辆标记为终止状态。*/
                            else if(checkIfTerminate_1st(car,entry.getValue(),i,j))
                                car.setState(0);
                                /* c)车辆如果行驶过程中，发现前方有车辆阻挡，且阻挡的车辆为等待车辆，
                                则该辆车也被标记为等待行驶车辆。（与阻挡车辆的距离s < v*t)）
                                其中：v=min(最大车速，道路限速),t=1  */
                            else if(checkIfWaiting_1st(car,entry.getValue(),i,j)) {
                                car.setState(1);
                                carsWaitingList.add(car);
                            }
                                /* d)车辆如果行驶过程中，发现前方有车辆阻挡，且阻挡的车辆为终止状态车辆，
                                则该辆车也被标记为终止车辆。（与前方阻挡的车辆的距离记为s）
                                则该车辆最大行驶速度为v = min(最高车速，道路限速，s/t) 其中t=1，该车辆最大可行驶距离为s。*/
                            else if(checkIfTerminate_2st(car,entry.getValue(),i,j))
                                car.setState(0);
                        }
                    }
                }
            }
//            carsWaitingList.sort(new Comparator<Car>() {
//                @Override
//                public int compare(Car o1, Car o2) {
//                    return o1.getId() - o2.getId();
//                }
//            });


            while (carsWaitingList.size() != 0){//直到所有车终止才停止调度
                //处理所有路口、道路中处于等待状态的车辆，等待车辆的调度顺序按7、8、9进行调度
                /* 7.整个系统调度按路口ID升序进行调度各个路口，路口内各道路按道路ID升序进行调度。每个路口遍历道路时，只调度该道路出路口的方向。*/
                for(Integer crossOrder : crossList){
                    List<Car> carsWaitingInCrossList = new ArrayList<>();
                    for(Integer roadId : crosses.get(crossOrder).getRoadSorted()){
                        if(roadId == -1)
                            continue;
                        Road currentRoad = roads.get(roadId);
                        for(int i = 0; i < currentRoad.getChannel(); i++){
                            for(int j = 0; j < currentRoad.getLength(); j++){
                                if(currentRoad.getCarsOnRoad()[i][j] != null && currentRoad.getCarsOnRoad()[i][j].getState() == 1){
                                    carsWaitingInCrossList.add(currentRoad.getCarsOnRoad()[i][j]);
                                }
                            }
                        }
                    }

                    while (carsWaitingInCrossList.size() != 0){
                        for(Integer roadId : crosses.get(crossOrder).getRoadSorted()){
                            if(roadId == -1)
                                continue;
                            Road currentRoad = roads.get(roadId);
                            for(int j = currentRoad.getLength() - 1; j >= 0; j--){
                                for(int i = 0; i < currentRoad.getChannel(); i++){

                                }
                            }


                        }
                    }

                }
            }


        }


        //当前时间点出发的车上路or等待上路
        for(Integer i : carList){
            if(t == i){

            }
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
    private static int min(int speed1, int speed2){
        if(speed1 < speed2)
            return speed1;
        else
            return speed2;
    }
    private static boolean checkIfTerminate_1st(Car car, Road road, int i, int j){
        int speed = min(car.getSpeed(),road.getSpeed());
        for(int k = j+1; k <= j + speed; k++){
            if(road.getCarsOnRoad()[i][k] != null)
                return false;
        }
        return true;
    }
    private static boolean checkIfWaiting_1st(Car car, Road road, int i, int j){
        int speed = min(car.getSpeed(),road.getSpeed());
        for(int k = j+1; k <= j + speed; k++){
            if(road.getCarsOnRoad()[i][k] != null && road.getCarsOnRoad()[i][k].getState() == 1){
                return true;
            }
        }
        return false;
    }
    private static boolean checkIfTerminate_2st(Car car, Road road, int i, int j){
        int speed = min(car.getSpeed(),road.getSpeed());
        for(int k = j+1; k <= j + speed; k++){
            if(road.getCarsOnRoad()[i][k] != null && road.getCarsOnRoad()[i][k].getState() == 0){
                road.getCarsOnRoad()[i][j] = null;
                road.getCarsOnRoad()[i][k - 1] = car;
                return true;
            }
        }
        return false;
    }
}