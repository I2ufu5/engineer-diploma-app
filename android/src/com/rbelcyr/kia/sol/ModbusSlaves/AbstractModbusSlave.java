package com.rbelcyr.kia.sol.ModbusSlaves;


import android.util.Log;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.Modbus;
import com.serotonin.modbus4j.exception.IllegalDataAddressException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;

import java.util.ArrayList;

public abstract class AbstractModbusSlave {

    private int port;
    private int slaveId;
    private TcpSlave tcpSlave;
    private boolean isRunning;
    private int coilsQuantity;
    private int inputsQuantity;


    public AbstractModbusSlave(){
        port = 10502;
        slaveId = 1;
        tcpSlave = new TcpSlave(port,false);
        try {
            tcpSlave.addProcessImage(new BasicProcessImage(slaveId));
        }catch (NullPointerException e) {
            Log.e("NULL SLAVE ID: ", e.toString());
        }
    }

    protected void addModbusInputs(int quantity){
        inputsQuantity = quantity;
        BasicProcessImage bpi = (BasicProcessImage) tcpSlave.getProcessImage(slaveId);
        for(int offset=0;offset<quantity;offset++){
            bpi.setInput(offset,false);
        }

    }

    protected void addModbusCoils(int quantity){
        coilsQuantity = quantity;
        BasicProcessImage bpi = (BasicProcessImage) tcpSlave.getProcessImage(slaveId);
        for(int offset=0;offset<quantity;offset++){
            bpi.setCoil(offset,false);
        }
    }

    public void startSlaveListener() throws InterruptedException{
        Thread thread;
        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    tcpSlave.start();
                    isRunning = true;
                } catch (ModbusInitException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        if(isRunning) synchronized (tcpSlave) {
            tcpSlave.wait(5000);
            Log.e("TAG","Listener Working");
        }
        else
            thread.interrupt();
    }

    public void stopSlaveListener(){
        tcpSlave.stop();
    }

    public ArrayList<Boolean> getAllCoils() throws IllegalDataAddressException {
        ArrayList<Boolean> list = new ArrayList<>();

        for(int i = 0; i<coilsQuantity ; i++){
            list.add(tcpSlave.getProcessImage(slaveId).getCoil(i));
        }

        return list;
    }

    public ArrayList<Boolean> getAllInputs() throws IllegalDataAddressException{
        ArrayList<Boolean> list = new ArrayList<>();

        for(int i = 0; i<inputsQuantity ; i++){
            list.add(tcpSlave.getProcessImage(slaveId).getInput(i));
        }

        return list;
    }

    public void setInput(int offset, boolean state){
        BasicProcessImage processImage = (BasicProcessImage) tcpSlave.getProcessImage(slaveId);
        processImage.setInput(offset,state);
    }

    public void switchInput(int offset) throws IllegalDataAddressException {
        BasicProcessImage bpi = (BasicProcessImage) tcpSlave.getProcessImage(slaveId);
        bpi.setInput(offset,!bpi.getInput(offset));
    }


}