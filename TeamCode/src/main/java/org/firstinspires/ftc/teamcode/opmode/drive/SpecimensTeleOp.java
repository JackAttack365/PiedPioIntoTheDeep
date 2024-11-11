package org.firstinspires.ftc.teamcode.opmode.drive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.robot.Config;
import org.firstinspires.ftc.teamcode.hardware.robot.ManualRobot;
import org.firstinspires.ftc.teamcode.enums.CycleTarget;
import org.firstinspires.ftc.teamcode.enums.GameStage;

@Disabled
@TeleOp(name="Specimens TeleOp", group="TeleOp")
public class SpecimensTeleOp extends OpMode {
    ManualRobot robot;
    Config config;
    FtcDashboard dashboard;

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();

        config = new Config(telemetry, dashboard, hardwareMap, gamepad1, gamepad2, GameStage.TeleOp, CycleTarget.SPECIMEN);
        robot = new ManualRobot(config);

        robot.init();
    }

    @Override
    public void loop() {
        config.updateTelemetry();

        robot.update();

        telemetry.update();
    }
}