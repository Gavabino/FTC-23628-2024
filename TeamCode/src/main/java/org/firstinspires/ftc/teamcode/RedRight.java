package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@Autonomous(preselectTeleOp = "Main")
public class RedRight extends LinearOpMode {
    private final int READ_PERIOD = 1;

    private HuskyLens huskyLens;

    @Override
    public void runOpMode() {
        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");
        Deadline rateLimit = new Deadline(READ_PERIOD, TimeUnit.SECONDS);
        rateLimit.expire();

        if (!huskyLens.knock()) {
            telemetry.addData(">>", "Problem communicating with " + huskyLens.getDeviceName());
        } else {
            telemetry.addData(">>", "Press start to continue");
        }

        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        telemetry.update();

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        CRServo hand = hardwareMap.get(CRServo.class, "hand");

        waitForStart();

        if (isStopRequested()) return;

        Pose2d startPose = new Pose2d(13.85, -62.77, Math.toRadians(90.00));

        drive.setPoseEstimate(startPose);

        TrajectorySequence leftPos = drive.trajectorySequenceBuilder(startPose)
                .forward(24,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(Math.toRadians(55))
                .forward(8.5)
                .build();

        Trajectory middlePos = drive.trajectoryBuilder(startPose)
                .forward(36,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence rightPos = drive.trajectorySequenceBuilder(startPose)
                .forward(24,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(Math.toRadians(-55))
                .forward(7,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();
        Trajectory returnMiddle = drive.trajectoryBuilder(middlePos.end())
                .back(8,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence returnRight = drive.trajectorySequenceBuilder(rightPos.end())
                .back(10,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(Math.toRadians(55))
                .build();

        Trajectory park = drive.trajectoryBuilder(new Pose2d())
                .strafeRight(40,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        TrajectorySequence returnLeft = drive.trajectorySequenceBuilder(leftPos.end())
                .back(8,
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .turn(Math.toRadians(35))
                .build();

        TrajectorySequence middlePixel = drive.trajectorySequenceBuilder(returnMiddle.end())
                .turn(Math.toRadians(90))
                .back(40)
                .strafeRight(5)
                .back(10)
                .build();

        TrajectorySequence rightPixel = drive.trajectorySequenceBuilder(returnRight.end())
                .turn(Math.toRadians(90))
                .back(50)
                .strafeRight(5)
                .back(3)
                .build();

        TrajectorySequence leftPixel = drive.trajectorySequenceBuilder(returnLeft.end())
                .back(45)
                .strafeRight(15)
                .back(5.5)
                .build();

        TrajectorySequence leftPark = drive.trajectorySequenceBuilder(leftPixel.end())
                .forward(10)
                .strafeRight(15)
                .back(5)
                .build();

        TrajectorySequence middlePark = drive.trajectorySequenceBuilder(middlePixel.end())
                .forward(10)
                .strafeRight(15)
                .back(5)
                .build();

        TrajectorySequence rightPark = drive.trajectorySequenceBuilder(rightPixel.end())
                .forward(10)
                .strafeRight(20)
                .back(5)
                .build();

        while(opModeIsActive()) {
            if (!rateLimit.hasExpired()) {
                continue;
            }
            rateLimit.reset();

            /*
             * All algorithms, except for LINE_TRACKING, return a list of Blocks where a
             * Block represents the outline of a recognized object along with its ID number.
             * ID numbers allow you to identify what the device saw.  See the HuskyLens documentation
             * referenced in the header comment above for more information on IDs and how to
             * assign them to objects.
             *
             * Returns an empty array if no objects are seen.
             */
            HuskyLens.Block[] blocks = huskyLens.blocks();
            telemetry.addData("Block count", blocks.length);
            for (HuskyLens.Block block : blocks) {
                telemetry.addData("Block", block.toString());
                int thisColorID = block.id;                      // save the current recognition's Color ID
                telemetry.addData("This Color ID", thisColorID);     // display that Color ID
                telemetry.update();
            }
            if (blocks.length >= 1) {
                for (HuskyLens.Block block : blocks) {
                    if ((block.id == 1 || block.id == 2) && (block.x > 150)) {
                        //When middle position is detected, place the pixel and then return

                        telemetry.addLine("Middle Position");
                        telemetry.update();
                        drive.followTrajectory(middlePos);
                        sleep(100);
                        drive.followTrajectory(returnMiddle);
                        sleep(100);
                        drive.followTrajectorySequence(middlePixel);
                        sleep(100);
                        hand.setPower(-1);
                        sleep(1300);
                        hand.setPower(1);
                        sleep(1000);
                        drive.followTrajectorySequence(middlePark);
                        requestOpModeStop();

                    } else if (blocks[0].x < 150) {
                        //When left position is detected, place the pixel and then return

                        telemetry.addLine("Left Position");
                        telemetry.update();
                        drive.followTrajectorySequence(leftPos);
                        sleep(100);
                        drive.followTrajectorySequence(returnLeft);
                        sleep(100);
                        drive.followTrajectorySequence(leftPixel);
                        sleep(100);
                        hand.setPower(-1);
                        sleep(1300);
                        hand.setPower(1);
                        sleep(1000);
                        drive.followTrajectorySequence(leftPark);
                        requestOpModeStop();
                    }
                }
            } else {
                //When right position is detected, place the pixel and then return

                telemetry.addLine("Right Position");
                telemetry.update();
                drive.followTrajectorySequence(rightPos);
                sleep(100);
                drive.followTrajectorySequence(returnRight);
                sleep(100);
                drive.followTrajectorySequence(rightPixel);
                hand.setPower(-1);
                sleep(1300);
                hand.setPower(1);
                sleep(1000);
                drive.followTrajectorySequence(rightPark);
                requestOpModeStop();
            }
            telemetry.update();

        }
    }
}
