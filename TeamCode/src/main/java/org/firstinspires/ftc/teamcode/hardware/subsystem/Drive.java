package org.firstinspires.ftc.teamcode.hardware.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.robot.Config;
import org.firstinspires.ftc.teamcode.hardware.Globals;
import org.firstinspires.ftc.teamcode.utils.DrivePowersBundle;


public class Drive implements SubSystem {
    Config config = null;

    private DcMotor leftFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightFrontDrive;
    private DcMotor rightBackDrive;

    private DrivePowersBundle old;

    public Drive(Config config) {
        this.config = config;
    }

    public void init() {
        leftFrontDrive = config.hardwareMap.get(DcMotor.class, Globals.Drive.LEFT_FRONT_DRIVE);
        rightFrontDrive = config.hardwareMap.get(DcMotor.class, Globals.Drive.RIGHT_FRONT_DRIVE);
        leftBackDrive = config.hardwareMap.get(DcMotor.class, Globals.Drive.LEFT_BACK_DRIVE);
        rightBackDrive = config.hardwareMap.get(DcMotor.class, Globals.Drive.RIGHT_BACK_DRIVE);

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        old = new DrivePowersBundle(0,0,0,0);
    }

    @Override
    public void start() {

    }

    public void update() {
        double y = -config.gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = config.gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = config.gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), Globals.Drive.MAX_SPEED);

        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        DrivePowersBundle now = new DrivePowersBundle(frontLeftPower, frontRightPower, backLeftPower, backRightPower);

        if (old.moved() && !now.moved()) {
            frontLeftPower = -old.FL;
            frontRightPower = -old.FR;
            backLeftPower = -old.BL;
            backRightPower = -old.BR;
        }

        leftFrontDrive.setPower(frontLeftPower);
        leftBackDrive.setPower(backLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        rightBackDrive.setPower(backRightPower);

        old = now;
    }
}