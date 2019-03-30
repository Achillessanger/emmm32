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
    private static int systemTime = 1;

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
        for (Map.Entry<Integer, Car> entry : cars.entrySet()){
            planRoute(entry.getValue());
        }

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
                line=String.copyValueOf(line.toCharArray(),1,line.length()-2);
                String[] lines=line.split(",");
                for (int i = 0; i <lines.length ; i++) {
                    lines[i]=lines[i].trim();
                }
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
            logger.info(e.getStackTrace());
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

    public static void judge() {
        //所有能走的车走掉并变成终止状态，不能走的变成等待状态

//        List<Integer> carsOnRoad = new ArrayList<>();
        List<Car> carsWaitingList = new ArrayList<>();
//        runningCarList.clear();
        int t = 0; //时间片
        while (!areAllCarsGoToTerminals()) {


            //先处理每条道路上的车辆，
            // 将这些车辆进行遍历扫描，

            for (Map.Entry<Integer, Road> entry : roads.entrySet()) {
                for (int i = 0; i < entry.getValue().getChannel(); i++) { //车道
                    for (int j = entry.getValue().getLength() - 1; j >= 0; j--) { //在车道上的位置
                        if (entry.getValue().getCarsOnRoad()[i][j] == null)
                            continue;
                        else {
                            Car car = entry.getValue().getCarsOnRoad()[i][j];
                            // 如果车在经过行驶速度（前方没有车辆阻挡）可以出路口，
                            // 将这些车辆标记为等待行驶车辆。
                            if (min(car.getSpeed(), entry.getValue().getSpeed()) > entry.getValue().getLength() - 1 - j && noCarsInfront(i, j, entry.getValue())) {
                                car.setState(1);
                                carsWaitingList.add(car);
                            }
                                /* b)车辆如果行驶过程中，前方没有阻挡并且也不会出路口（v=min(最大车速，道路限速)），
                                则该车辆行驶可行驶的最大车速（v=min(最大车速，道路限速)），
                                此时该车辆在本次调度确定了该时刻的终止位置。该车辆标记为终止状态。*/
                            else if (checkIfTerminate_1st(car, entry.getValue(), i, j))
                                car.setState(0);
                                /* c)车辆如果行驶过程中，发现前方有车辆阻挡，且阻挡的车辆为等待车辆，
                                则该辆车也被标记为等待行驶车辆。（与阻挡车辆的距离s < v*t)）
                                其中：v=min(最大车速，道路限速),t=1  */
                            else if (checkIfWaiting_1st(car, entry.getValue(), i, j)) {
                                car.setState(1);
                                carsWaitingList.add(car);
                            }
                                /* d)车辆如果行驶过程中，发现前方有车辆阻挡，且阻挡的车辆为终止状态车辆，
                                则该辆车也被标记为终止车辆。（与前方阻挡的车辆的距离记为s）
                                则该车辆最大行驶速度为v = min(最高车速，道路限速，s/t) 其中t=1，该车辆最大可行驶距离为s。*/
                            else if (checkIfTerminate_2st(car, entry.getValue(), i, j))
                                car.setState(0);
                        }
                    }
                }
            }

            while (carsWaitingList.size() != 0) {
                //直到所有车终止才停止调度
                //处理所有路口、道路中处于等待状态的车辆，等待车辆的调度顺序按7、8、9进行调度
                /* 7.整个系统调度按路口ID升序进行调度各个路口，路口内各道路按道路ID升序进行调度。每个路口遍历道路时，只调度该道路出路口的方向。*/
                for (Integer crossOrder : crossList) { //路口
                    //对该路口进行调度
                    for (Integer roadId : crosses.get(crossOrder).getRoadSorted()) { //四条路
                        if (roadId == -1)
                            continue;
                        Road currentRoad = roads.get(roadId);
                        int channel = 0;
                        Car car = null;
                        int forSpace = 0;
                        int carJ = 0;
                        int carI = 0;
                        Loop:
                        for (int j = currentRoad.getLength() - 1; j >= 0; j--) {
                            for (int i = 0; i < currentRoad.getChannel(); i++) {
                                if (currentRoad.getCarsOnRoad()[i][j] == null)
                                    continue;
                                else {
                                    channel = i;
                                    carI = i;
                                    carJ = j;
                                    forSpace = currentRoad.getLength() - 1 - j;
                                    car = currentRoad.getCarsOnRoad()[i][j];
                                    break Loop;
                                }
                            }
                        }
                        if (car == null)
                            continue;
                        if (isConflict(car, crosses.get(crossOrder), currentRoad)) {
                            continue;
                        }
                        moveCarToNextRoad(car, crosses.get(crossOrder), channel, currentRoad, forSpace);
                        car.getRoute().remove(0);
                        currentRoad.getCarsOnRoad()[carI][carJ] = null;
                        car.setState(0);
                        carsWaitingList.remove(car);


                        for (int j = currentRoad.getLength() - 1; j >= 0; j--) { //在车道上的位置
                            if (currentRoad.getCarsOnRoad()[channel][j] == null)
                                continue;
                            else {
                                Car car2 = currentRoad.getCarsOnRoad()[channel][j];
                                if (check_3st(car2, crosses.get(crossOrder), currentRoad)) {
                                    currentRoad.getCarsOnRoad()[channel][j] = null;
                                    currentRoad.getCarsOnRoad()[channel][currentRoad.getLength() - 1] = car2;
                                    car2.setState(0);
                                    carsWaitingList.remove(car2);
                                } else if (checkIfTerminate_1st(car2, currentRoad, channel, j)) {
                                    car2.setState(0);
                                    carsWaitingList.remove(car2);
                                } else if (checkIfTerminate_2st(car2, currentRoad, channel, j)) {
                                    car2.setState(0);
                                    carsWaitingList.remove(car2);
                                }
                            }
                        }
                    }
                }
            }

            /* 车库中的车辆上路行驶 */
            for (Map.Entry<Integer, Car> entry : cars.entrySet()) {
                Car car = entry.getValue();
                Road road = car.getRoute().get(0);
                if (car.getStartTimeInAnswerSheet() == t) {
                    boolean canGo = false;
                    int carI = 0;
                    for (int i = 0; i < road.getChannel(); i++) {
                        if (road.getCarsOnRoad()[i][0] == null) {
                            carI = i;
                            road.getCarsOnRoad()[i][0] = car;
                            canGo = true;
                        }
                    }
                    if (!canGo) {
                        throw new RuntimeException("车无法上路");
                    }
                    int s1 = 0;
                    for (int j = 1; j < road.getLength(); j++) {
                        if (road.getCarsOnRoad()[carI][j] == null) {
                            s1++;
                        } else {
                            break;
                        }
                    }
                    int newJ = min(s1, car.getSpeed());
                    road.getCarsOnRoad()[carI][newJ] = car;
                    road.getCarsOnRoad()[carI][0] = null;
                    car.setState(0);
                }
            }
        }

        t++;

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
        road.getCarsOnRoad()[i][j] = null;
        road.getCarsOnRoad()[i][j+speed] = car;
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
    private static int direction(Car car){
        Road road1 = car.getRoute().get(0);
        Road road2 = car.getRoute().get(1);
        Cross cross = crosses.get(road1.getTo());
        int road1Order = -1;
        int road2Order = -1;
        for (int i = 0; i < 4; i++){
            if(road1.getId() == cross.getRoad()[i])
                road1Order = i;
            if(road2.getId() == cross.getRoad()[i])
                road2Order = i;
        }
        if(road2Order - road1Order == 2 || road2Order - road1Order == -2)
            return 3;//直行
        else if(road2Order - road1Order == -3 || road2Order - road1Order == 1)
            return 2;//左转
        else
            return 1;//右转 2 - 1 = -1 or 3

    }
    private static boolean checkCross(Cross cross){ //false=四车道第一优先级都不能动
        boolean ret = true;

        return ret;

    }
    private static Car getPrimaryWaitingCar(Road road){
        for(int j = road.getLength() - 1; j >= 0; j--){
            for(int i = 0; i < road.getChannel(); i++) {
                if(road.getCarsOnRoad()[i][j] == null)
                    continue;
                else
                    return road.getCarsOnRoad()[i][j];
            }
        }
        return null;
    }
    private static Road getNowRoad(Cross cross,Car car){

        return null;
    }

    private static Road getNextRoad(Cross cross, Car car, Road road){
        int dir = direction(car);
        int carRoadOrder = -1;
        for (int i = 0; i < 4; i++){
            if(cross.getRoad()[i] == road.getId()){
                carRoadOrder = i;
                break;
            }
        }
        Road nextRoad = null;
        int nextCarRoadOrder = -1;
        if(dir == 3){
            nextCarRoadOrder = ((carRoadOrder + 2) >= 0 && (carRoadOrder + 2) < 4)?(carRoadOrder + 2):(carRoadOrder - 2);
        }else if(dir == 2){
            nextCarRoadOrder = ((carRoadOrder + 1) >= 0 && (carRoadOrder + 1) < 4)?(carRoadOrder + 1):(carRoadOrder - 3);
        }else
            nextCarRoadOrder = ((carRoadOrder + 3) >= 0 && (carRoadOrder + 3) < 4)?(carRoadOrder + 3):(carRoadOrder - 1);

        nextRoad = (roads.get(cross.getRoad()[nextCarRoadOrder]).getFrom() == cross.getId())?roads.get(cross.getRoad()[nextCarRoadOrder]):roads.get(cross.getRoad()[nextCarRoadOrder]).getRoadReverse();

        return nextRoad;
    }

    private static boolean isConflict(Car car, Cross cross, Road road){
        int dir = direction(car);
        for(int i = 0; i < 4; i++){
            if(cross.getRoad()[i] == road.getId() || cross.getRoad()[i] == -1)
                continue;
            else if(direction(getPrimaryWaitingCar(roads.get(cross.getRoad()[i]))) > dir){
                return true;
            }
        }
        return false;
    }
    private static void moveCarToNextRoad(Car car,Cross cross,int channel ,Road thisRoad,int forSpace){
        Road nextRoad = getNextRoad(cross,car,thisRoad);
        int willChannel = 0;
        int b = 0;
        Loop:
        for(int i = 0; i < nextRoad.getChannel(); i++){
            for(int j = 0; j < nextRoad.getLength(); j++){
                if(nextRoad.getCarsOnRoad()[i][j] == null){
                    willChannel = i;
                    break Loop;
                }
            }
        }

        int s1 = car.getSpeed() - forSpace;
        int s2 = nextRoad.getSpeed() - forSpace;

        int canSpace = nextRoad.getLength();
        Car frontCar = null;
        for(int j = 0; j < nextRoad.getLength(); j++){
            if(nextRoad.getCarsOnRoad()[willChannel][j] == null)
                continue;
            else {
                frontCar = nextRoad.getCarsOnRoad()[willChannel][j];
                canSpace = j;
                break;
            }
        }
        if(canSpace < s2) {
            s2 = canSpace;
            if(frontCar.getState() == 1)
                throw new RuntimeException("我觉得此处有锁");
        }
        if(canSpace < s1) {
            s1 = canSpace;
            if(frontCar.getState() == 1)
                throw new RuntimeException("我觉得此处有锁");
        }

        int newJ = min(s1,s2) - 1;
        if(newJ + 1 == 0)
            throw new RuntimeException("拐弯的车拐不进去");

        nextRoad.getCarsOnRoad()[willChannel][newJ] = car;


    }
    private static boolean check_3st(Car car, Cross cross, Road road){
//        min(car.getSpeed(),entry.getValue().getSpeed()) > entry.getValue().getLength() - 1 - j;
        Road nextRoad = getNextRoad(cross,car,road);
        int forSpace = 0;
        Loop:
        for(int j = road.getLength() - 1; j >= 0; j--){
            for(int i = 0; i < road.getChannel(); i++){
                if(road.getCarsOnRoad()[i][j] == car){
                    forSpace = road.getLength() - 1 - j;
                    break Loop;
                }
            }
        }

        if(min(nextRoad.getSpeed(),car.getSpeed()) - forSpace == 0 && min(road.getSpeed(),car.getSpeed()) > forSpace)
            return true;
        else
            return false;

    }
    private static boolean noCarsInfront( int i, int j, Road road){
        for(int k = j + 1; k < road.getLength(); k++){
            if(road.getCarsOnRoad()[i][k] != null)
                return false;
        }
        return true;
    }

    public static void planRoute(Car car){
        Cross start = crosses.get(car.getFrom().getId());
        Cross end = crosses.get(car.getTo().getId());
        Dijkstra(start, end);
        int time = start.getMin();
        car.setStartTimeInAnswerSheet(systemTime);
        systemTime = systemTime + time;

        ArrayList<Road> route = car.getRoute();
        while (true){
            for (int i = 0; i < end.getRoad().length; i++){
                Road road = roads.get(end.getRoad()[i]);
                if (road.getTo() == end.getId()
                        && road.getFrom() == end.getPre().getId()){
                    route.add(0, road);
                }
            }
            if (end.getPre() == null){
                break;
            }
            end = end.getPre();
        }
    }

    public static void Dijkstra(Cross start, Cross end){
        //初始化
        //对起点初始化
        start.setMin(0);
        //构造优先队列
        Queue<Cross> queue = createPriorityQueue();
        while (!queue.isEmpty()){
            if (end.isDealt())
                break;
            Cross cross = queue.poll();
            cross.setDealt(true);
            int[] nextRoads = cross.getRoad();
            for (int i = 0; i < nextRoads.length; i++){
                Road road = roads.get(nextRoads[i]);
                int time = road.getTime();
                Cross nextCross = findNextCross(cross, road);
                if ((nextCross.getMin() > cross.getMin() + time) && !nextCross.isDealt()){
                    nextCross.setMin(cross.getMin() + time);
                    nextCross.setPre(cross);
                    queue.remove(nextCross);
                    queue.add(nextCross);
                }
            }
        }
    }

    public static Cross findNextCross(Cross cross, Road road){
        if (road.getFrom() == cross.getId()){
            //说明road的起点即为该路口
            return crosses.get(road.getTo());
        }
        return null;
    }

    public static Queue<Cross> createPriorityQueue(){
        //实现优先队列
        Queue<Cross> station = new PriorityQueue<>();
        for (Map.Entry<Integer, Cross> entry : crosses.entrySet()){
            Cross next = entry.getValue();
            station.add(next);
        }
        return station;
    }
}