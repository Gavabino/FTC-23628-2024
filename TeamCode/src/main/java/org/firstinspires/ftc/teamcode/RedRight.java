package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

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

        waitForStart();

        if (isStopRequested()) return;

        Pose2d startPose = new Pose2d(13.85, -62.77, Math.toRadians(90.00));

        Vector2d startPoseVector = new Vector2d(13.85, -62.77);

        drive.setPoseEstimate(startPose);

        Trajectory leftPos = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-47.38, -30.86), Math.toRadians(111.27),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory middlePos = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(12.88, -27), Math.toRadians(98.32),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory rightPos = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(23.57, -37.82), Math.toRadians(98.32),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory returnMiddle = drive.trajectoryBuilder(middlePos.end(), true)
                .splineTo(startPoseVector, Math.toRadians(90),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory returnLeft = drive.trajectoryBuilder(leftPos.end(), true)
                .splineTo(startPoseVector, Math.toRadians(90),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory returnRight = drive.trajectoryBuilder(rightPos.end(), true)
                .splineTo(startPoseVector, Math.toRadians(90),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory park = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(58.88, -59.04), Math.toRadians(-6.34),
                        SampleMecanumDrive.getVelocityConstraint(20, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
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
                    if ((block.id == 2) && (block.x > 150)) {
                        //When middle position is detected, place the pixel and then return

                        telemetry.addLine("Middle Position");
                        telemetry.update();
                        drive.followTrajectory(middlePos);
                        sleep(100);
                        drive.followTrajectory(returnMiddle);
                        sleep(100);
                        drive.followTrajectory(park);
                        requestOpModeStop();

                    } else if (blocks[0].x < 150) {
                        //When left position is detected, place the pixel and then return

                        telemetry.addLine("Left Position");
                        telemetry.update();
                        drive.followTrajectory(leftPos);
                        sleep(100);
                        drive.followTrajectory(returnLeft);
                        sleep(100);
                        drive.followTrajectory(park);
                        requestOpModeStop();
                    }
                }
            } else {
                //When right position is detected, place the pixel and then return

                telemetry.addLine("Right Position");
                telemetry.update();
                /*drive.followTrajectory(rightPos);
                sleep(100);
                drive.followTrajectory(returnRight);
                sleep(100);
                drive.followTrajectory(park);
                requestOpModeStop();*/
            }
            telemetry.update();

        }
    }
}
