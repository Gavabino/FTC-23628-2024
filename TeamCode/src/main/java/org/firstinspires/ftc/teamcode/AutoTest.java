package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@Autonomous
@Disabled
public class AutoTest extends LinearOpMode {

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

        Pose2d startPose = new Pose2d(-36.04, -62.93, Math.toRadians(90.00));

        drive.setPoseEstimate(startPose);

        Trajectory leftPos = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-47.38, -30.86), Math.toRadians(111.27))
                .build();

        Trajectory middlePos = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-36.20, -27.00), Math.toRadians(88.68))
                .build();

        Trajectory reverseMiddle = drive.trajectoryBuilder(middlePos.end())
                .back(24)
                .build();

        Trajectory reverseLeft = drive.trajectoryBuilder(leftPos.end())
                .back(24)
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
            }
            if (blocks.length >= 1) {
                for (HuskyLens.Block block : blocks) {
                    if ((block.id == 2) && (block.x > 150)) {
                        telemetry.addLine("Middle Position");
                        drive.followTrajectory(middlePos);
                        sleep(100);
                        drive.followTrajectory(reverseMiddle);
                        requestOpModeStop();
                    } else if (blocks[0].x < 150) {
                        telemetry.addLine("Left Position");
                        drive.followTrajectory(leftPos);
                        sleep(100);
                        drive.followTrajectory(reverseLeft);
                        requestOpModeStop();
                    }
                }
            } else {
                telemetry.addLine("Right Position");
            }
            telemetry.update();

        }
    }
}
