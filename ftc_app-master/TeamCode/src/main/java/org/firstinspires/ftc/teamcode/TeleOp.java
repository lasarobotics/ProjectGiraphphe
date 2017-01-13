/*Code based on:

https://github.com/lasarobotics/FTC4290-2014-Library/blob/2be3ec3ac60823473f3070e147acc7833ca0d414/robotc/src/4290-Teleop.c#L130

*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;

/**
 * Created by Ethan Schaffer and Abhijay Saini on 1/10/2017.
 */

public class TeleOp extends OpMode{
    private DcMotor leftFront, leftBack, rightFront, rightBack, intake, blowerA, blowerB, blowerC;
    private Servo ballStorage, tubeWinch, kickstand, touchSensor, goalRetainer;
    private boolean touchsensorenabled = false, blowerenabled = false, kickstandenabled = false,
            storageclosed = false, intakeenabled = false,
            lastBButtonState = false, lastYButtonState = false, lastAButtonState = false,
            joy2Btn1last = false, joy2Btn2last = false, joy2Btn3last = false, joy2Btn4last = false;
    private DcMotor blowers[] = {blowerA, blowerB, blowerC};
    @Override
    public void init() {
        leftFront = hardwareMap.dcMotor.get("lf");
        leftBack= hardwareMap.dcMotor.get("lb");
        rightBack = hardwareMap.dcMotor.get("rf");
        rightFront = hardwareMap.dcMotor.get("rb");
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        intake = hardwareMap.dcMotor.get("i");
        blowerA = hardwareMap.dcMotor.get("ba");
        blowerB = hardwareMap.dcMotor.get("bb");
        blowerC = hardwareMap.dcMotor.get("bc");
        ballStorage = hardwareMap.servo.get("bs");
        tubeWinch = hardwareMap.servo.get("tw");
        kickstand = hardwareMap.servo.get("k");
        touchSensor = hardwareMap.servo.get("ts");
        goalRetainer = hardwareMap.servo.get("gr");
        servoSetPos(tubeWinch, 127);
        servoSetPos(goalRetainer, 5);
        servoSetPos(kickstand, 155);
        servoSetPos(ballStorage, 80);
        servoSetPos(touchSensor, 65);
    }

    public void servoSetPos(Servo s, double p){
        s.setPosition(p/255);
    }

    /*Control Layout:
    Controller 1:
    / Left Joystick x/y - Strafe and forward for robot
    / Right Joystick x - Turn
    Button 1: Goal Latch Closed
    Button 3: Goal Latch Open
    Button 8: Slo-Mo
    Controller 2:
    / Button 1: Blower
    / Button 2: Intake
    Button 3: Ball storage
    Button 4: Kickstand
    Button 5: Intake slow (lift release)
    Button 6: Intake backwards
    Button 7: Touch sensor
    */

    @Override
    public void loop() {
        arcade(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, leftFront, rightFront, leftBack, rightBack);

        /*
        * Intake infeeds until we press the gamepad A Button RIGHT???
        * BLowers work by pressing gamepad B Button. If pressed the second time, stop it
        * Deleted extra assignments
        * I replaced some if-else
        */

        if (gamepad1.a)
        {
            if (intake.getPower() == 0)
            {
                intake.setPower(-0.5);
            }
        }else{
            if (intake.getPower() == 1)
            {
                intake.setPower(0);
            }
        }

        if (gamepad1.y)
        {
            if (intake.getPower() == 0)
            {
                intake.setPower(0.5);
            }
        }else{
            if (intake.getPower() == 1)
            {
                intake.setPower(0);
            }
        }
        /*
        ________ _____  _____  _____ ___    
        |__  __||     ||      |      | |
          |  |  |  -  ||   __ |   __ | |
          |  |  |  -  ||     ||     || |___
          ----   -----  -----  ----- |_____|
        if (gamepad1.a)
        {
            if(intake.getPower() == 0)
            {
                intake.setPower(-0.5);
            }else{
                intake.setPower(0);
            }
        } else if (gamepad1.y)
        {
            if(intake.getPower() == 0)
            {
                intake.setPower(0.5);
            }else{
                intake.setPower(0);
            }
        }
        */

        /*
        REPLACABLE
        if (gamepad1.a && !lastAButtonState)
        {
            if(intake.getPower() == 0){
                intake.setPower(-0.5);
            } else {
                intake.setPower(0);
            }

        }

        if(gamepad1.y && !lastYButtonState){
            if (intake.getPower() == 0)
            {
                intake.setPower(0.5);
            }else {
                intake.setPower(0);
            }
        }

        if(gamepad1.b && !lastBButtonState)
        {
            if (blowerA.getPower() == 0)
            {
                blowerA.setPower(1);
                blowerB.setPower(1);
                blowerC.setPower(1);
            } else {
                blowerA.setPower(0);
                blowerB.setPower(0);
                blowerC.setPower(0);
            }
        }
        */


        if (gamepad1.b)
        {
            if(blowerA.getPower() == 0)
            {
                blowerA.setPower(1);
                blowerB.setPower(1);
                blowerC.setPower(1);
            }else{
                coastMotors(blowers);
            }
        }


        /*
        DON't think we need this

        lastAButtonState = gamepad1.a;
        lastYButtonState = gamepad1.y;
        lastBButtonState = gamepad1.b;
        */
    }

    @Override
    public void stop(){
        if (blowerA.getPower() != 0)
        {
            coastMotors(blowers);
        }
    }

    //y = forwards
    //x = strafe
    //c = turn
    public static void arcade(double y, double x, double c, DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack) {
        double leftFrontVal = y + x + c;
        double rightFrontVal = y - x - c;
        double leftBackVal = y - x + c;
        double rightBackVal = y + x - c;

        //Move range to between 0 and +1, if not already
        double[] wheelPowers = {rightFrontVal, leftFrontVal, leftBackVal, rightBackVal};
        Arrays.sort(wheelPowers);
        if (wheelPowers[3] > 1) {
            leftFrontVal /= wheelPowers[3];
            rightFrontVal /= wheelPowers[3];
            leftBackVal /= wheelPowers[3];
            rightBackVal /= wheelPowers[3];
        }

        leftFront.setPower(leftFrontVal);
        rightFront.setPower(rightFrontVal);
        leftBack.setPower(leftBackVal);
        rightBack.setPower(rightBackVal);
    }
    public void coastMotors (DcMotor motors[])
            /* Supports manipulation of multiple motors*/
    {
        for (double power = 1; power >= 0; power -= 0.01)
        {
            for (int i = 0; i < motors.length; i++)
            {
                motors[i].setPower(power);
            }
        }
    }
}
