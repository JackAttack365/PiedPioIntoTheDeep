package org.firstinspires.ftc.teamcode.opmode.autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.robot.enums.Alliance;
import org.firstinspires.ftc.teamcode.hardware.robot.AutonomousRobot;
import org.firstinspires.ftc.teamcode.hardware.robot.Config;
import org.firstinspires.ftc.teamcode.hardware.robot.enums.CycleTarget;
import org.firstinspires.ftc.teamcode.hardware.robot.enums.GameStage;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

@Autonomous(name="Specimen Auto / 0+2 Auto", group="Autos")
public class SpecimenAuto extends LinearOpMode {
    Config config;

    AutonomousRobot robot;

    FtcDashboard dashboard;

    Pose2d startPose;

    MecanumDrive drive;

    TrajectoryActionBuilder driveToBar, pushToZone, toBar, park;

    @Override
    public void runOpMode() throws InterruptedException {
        startPose = new Pose2d(15, -62, Math.toRadians(180));

        dashboard = FtcDashboard.getInstance();

        config = new Config(telemetry, hardwareMap, gamepad1, gamepad2, GameStage.Autonomous, Alliance.RED);

        robot = new AutonomousRobot(config);

        drive = new MecanumDrive(hardwareMap, startPose);

        robot.init();

        driveToBar = drive.actionBuilder(startPose).strafeToConstantHeading(new Vector2d(9,-34));

        pushToZone = driveToBar.fresh()
                .strafeToConstantHeading(new Vector2d(10, -40))
                .splineToConstantHeading(new Vector2d(30, -37), Math.PI/4)
                .splineToConstantHeading(new Vector2d(36, -10), Math.PI/2)
                .turn(Math.toRadians(-90))
                .splineToConstantHeading(new Vector2d(46, -10), Math.PI/4)
                .strafeToConstantHeading(new Vector2d(46, -55))
                .strafeToConstantHeading(new Vector2d(46, -10))
                .splineToConstantHeading(new Vector2d(56, -10), Math.PI/4)
                .strafeToConstantHeading(new Vector2d(56, -55));

        park = pushToZone.fresh().strafeToConstantHeading(new Vector2d(9,-34));

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        robot.outtake.bar(),
                        driveToBar.build(),
                        robot.outtake.clip(),
                        new ParallelAction(
                                robot.outtake.down(),
                                pushToZone.build()
                        ),
                        park.build()
                )
        );
    }
}
