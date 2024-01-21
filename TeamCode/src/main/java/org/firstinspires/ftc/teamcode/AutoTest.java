package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;
@Autonomous
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

        Pose2d startPose = new Pose2d(-36.85, -61.96, Math.toRadians(90.00));

        drive.setPoseEstimate(startPose);

        Trajectory Test1 = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-35.88, -49.48), Math.toRadians(88.23))
                .splineTo(new Vector2d(11.91, -36.53), Math.toRadians(-0.97))
                .build();

        Trajectory MiddlePos = drive.trajectoryBuilder(startPose)
                .splineTo(new Vector2d(-36.20, -24.00), Math.toRadians(88.68))
                .build();

        Trajectory Reverse = drive.trajectoryBuilder(MiddlePos.end())
                .back(24)
                .build();

        Trajectory Test3 = drive.trajectoryBuilder(new Pose2d())
                .splineTo(new Vector2d(30, 0), 0)
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
            int elementX = 0;
            telemetry.addData("Block count", blocks.length);
            for (HuskyLens.Block block : blocks) {
                telemetry.addData("Block", block.toString());
                int thisColorID = block.id;                      // save the current recognition's Color ID
                telemetry.addData("This Color ID", thisColorID);     // display that Color ID
            }
            if (blocks.length >= 1) {
                for (HuskyLens.Block block : blocks) {
                    if ((block.id == 2) && (block.x > 150)) {
                        elementX = blocks[0].x;
                        telemetry.addLine("Middle Position");
                        drive.followTrajectory(MiddlePos);
                        sleep(100);
                        drive.followTrajectory(Reverse);
                        requestOpModeStop();
                    } else if (blocks[0].x < 150) {
                        telemetry.addLine("Left Position");
                        elementX = blocks[0].x;
                    }
                }
            } else {
                telemetry.addLine("Right Position");
            }
            telemetry.addData("X Pos", elementX);
            telemetry.update();

        }
    }
}
