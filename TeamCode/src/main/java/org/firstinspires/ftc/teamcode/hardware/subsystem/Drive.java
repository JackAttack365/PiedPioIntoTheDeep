package org.firstinspires.ftc.teamcode.hardware.subsystem;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Config;


public class Drive implements SubSystem {
    Config config = null;

    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    private IMU imu = null;

    public Drive(Config cfg) {
        this.config = config;
    }

    public void init() {
        // Retrieve the IMU from the hardware map
        imu = config.hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);


        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive = config.hardwareMap.get(DcMotor.class, "rightBack");
        rightFrontDrive = config.hardwareMap.get(DcMotor.class, "leftBack");
        leftBackDrive = config.hardwareMap.get(DcMotor.class, "leftFront");
        rightBackDrive = config.hardwareMap.get(DcMotor.class, "rightFront");

        // Most robots need the motors on one side to be reversed to drive forward.
        // When you first test your robot, push the left joystick forward
        // and flip the direction ( FORWARD <-> REVERSE ) of any wheel that runs backwards
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE); // DO NOT CHANGE
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE); // DO NOT CHANGE
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD); // DO NOT CHANGE
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD); // DO NOT CHANGE

        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // DO NOT CHANGE
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // DO NOT CHANGE
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // DO NOT CHANGE
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // DO NOT CHANGE
    }

    public void update() {
        double y = -config.gamePad1.left_stick_y; // Remember, Y stick value is reversed
        double x = config.gamePad1.left_stick_x;
        double rx = config.gamePad1.right_stick_x;

        // This button choice was made so that it is hard to hit on accident,
        // it can be freely changed based on preference.
        // The equivalent button is start on Xbox-style controllers.
        if (config.gamePad1.options) {
            imu.resetYaw();
        }

        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // Counteract imperfect strafing

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        leftFrontDrive.setPower(frontLeftPower);
        leftBackDrive.setPower(backLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        rightBackDrive.setPower(backRightPower);
    }
}