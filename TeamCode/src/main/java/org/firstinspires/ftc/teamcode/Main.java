package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Main")
public class Main extends LinearOpMode {

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor rightRear = hardwareMap.get(DcMotor.class, "rightRear");
        DcMotor leftRear = hardwareMap.get(DcMotor.class, "leftRear");
       // DcMotor slide1 = hardwareMap.get(DcMotor.class, "slide1");
       // DcMotor slide2 = hardwareMap.get(DcMotor.class, "slide2");
        Servo claw = hardwareMap.get(Servo.class, "claw");

        // Reverse the right side.
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        // This makes the robot BRAKE when power becomes zero. The other
        // mode, FLOAT, makes the robot go in neutral and will drift.
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //slide1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //slide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        claw.scaleRange(0,85);
        claw.setPosition(0);
       // slide1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       // slide1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      //  slide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       // slide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
               //telemetry.addData("Slide1 Position", slide1.getCurrentPosition());
                // telemetry.addData("Slide2 Position", slide2.getCurrentPosition());
                telemetry.addData("Claw Position", claw.getPosition());
                telemetry.update();
                if (gamepad1.left_stick_y > 0.5 && gamepad1.left_stick_x > 0.5) {
                    rightFront.setPower(-0.75);
                    leftFront.setPower(0);
                    rightRear.setPower(0);
                    leftRear.setPower(-0.75);
                } else if (gamepad1.left_stick_y > 0.5 && gamepad1.left_stick_x < -0.5) {
                    rightFront.setPower(0);
                    leftFront.setPower(-0.75);
                    rightRear.setPower(-0.75);
                    leftRear.setPower(0);
                } else if (gamepad1.left_stick_y < -0.5 && gamepad1.left_stick_x < -0.5) {
                    rightFront.setPower(0.75);
                    leftFront.setPower(0);
                    rightRear.setPower(0);
                    leftRear.setPower(0.75);
                } else if (gamepad1.left_stick_y < -0.5 && gamepad1.left_stick_x > 0.5) {
                    rightFront.setPower(0);
                    leftFront.setPower(0.75);
                    rightRear.setPower(0.75);
                    leftRear.setPower(0);
                } else if (gamepad1.left_stick_y > 0.5) {
                    rightFront.setPower(-0.75);
                    leftFront.setPower(-0.75);
                    rightRear.setPower(-0.75);
                    leftRear.setPower(-0.75);
                } else if (gamepad1.left_stick_y < -0.5) {
                    rightFront.setPower(0.75);
                    leftFront.setPower(0.75);
                    rightRear.setPower(0.75);
                    leftRear.setPower(0.75);
                } else if (gamepad1.left_stick_x > 0.5) {
                    rightFront.setPower(-0.75);
                    leftFront.setPower(0.75);
                    rightRear.setPower(0.75);
                    leftRear.setPower(-0.75);
                } else if (gamepad1.left_stick_x < -0.5) {
                    rightFront.setPower(0.75);
                    leftFront.setPower(-0.75);
                    rightRear.setPower(-0.75);
                    leftRear.setPower(0.75);
                } else if (gamepad1.right_bumper) {
                    rightFront.setPower(0.75);
                    leftFront.setPower(-0.75);
                    rightRear.setPower(0.75);
                    leftRear.setPower(-0.75);
                } else if (gamepad1.left_bumper) {
                    rightFront.setPower(-0.75);
                    leftFront.setPower(0.75);
                    rightRear.setPower(-0.75);
                    leftRear.setPower(0.75);
                } else {
                    rightFront.setPower(0);
                    leftFront.setPower(0);
                    rightRear.setPower(0);
                    leftRear.setPower(0);
                }
                if (gamepad1.right_stick_x > 0.5) {
                    rightFront.setPower(1);
                    leftFront.setPower(-1);
                    rightRear.setPower(1);
                    leftRear.setPower(-1);
                } else if (gamepad1.right_stick_x < -0.5) {
                    rightFront.setPower(-1);
                    leftFront.setPower(1);
                    rightRear.setPower(-1);
                    leftRear.setPower(1);
                }
                if (gamepad1.a) {
                    claw.setPosition(175);
                } else if (gamepad1.b) {
                    claw.setPosition(45);
                }
                /*
                if (gamepad1.x && slide1.getCurrentPosition() > 0) {
                    slide1.setPower(-0.9725);
                } else if (gamepad1.y && slide1.getCurrentPosition() < 3075) {
                    slide1.setPower(0.9725);
                } else {
                    slide1.setPower(0);
                }
                if (gamepad1.x && slide2.getCurrentPosition() < 0) {
                    slide2.setPower(1);
                } else if (gamepad1.y && slide2.getCurrentPosition() > -3075) {
                    slide2.setPower(-1);
                } else {
                    slide2.setPower(0);
                }
                */
            }
        }
    }
}
