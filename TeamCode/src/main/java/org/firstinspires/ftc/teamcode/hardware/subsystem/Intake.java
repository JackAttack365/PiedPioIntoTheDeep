package org.firstinspires.ftc.teamcode.hardware.subsystem;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.robot.Config;
import org.firstinspires.ftc.teamcode.hardware.Globals;

public class Intake implements TeleOpSubSystem {
    public enum IntakePosition {
        DUMPING,
        RETRACTED,
        INTAKING,
        RETRACTING,
        EXTENDING,
        EXTENDED
    }

    Config config;

    DcMotor intake;
    DcMotor extendo;

    Servo bucket;

    //ColorSensor sensor;

    IntakePosition status;

    public Intake(Config config) {this.config = config;}

    @Override
    public void init() {
        extendo = config.hardwareMap.get(DcMotor.class, Globals.Intake.EXTENDO_MOTOR);

        intake = config.hardwareMap.get(DcMotor.class, Globals.Intake.INTAKE_MOTOR);

        bucket = config.hardwareMap.get(Servo.class, Globals.Intake.INTAKE_SERVO);

        //sensor = config.hardwareMap.get(ColorSensor.class, "colorSensor");


        extendo.setDirection(DcMotorSimple.Direction.FORWARD);
        extendo.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        status = IntakePosition.RETRACTED;
    }

    @Override
    public void start() {
        bucket.setPosition(Globals.Intake.BUCKET_UP);
    }

    @Override
    public void update() {

        if (config.gamepad2.right_bumper && !(extendo.getCurrentPosition() >= Globals.Intake.EXTENDO_OUT)) {
            extendo.setPower(1);
        } else if (config.gamepad2.left_bumper && !(extendo.getCurrentPosition() <= Globals.Intake.EXTENDO_IN)) {
            extendo.setPower(-1);
        } else {
            extendo.setPower(0);
        }

        if (config.gamepad2.b) {
            bucket.setPosition(0.25);
            intake.setPower(0);

            extendo.setPower(-1);
        }

        if (config.gamepad2.a) {
            bucket.setPosition(Globals.Intake.BUCKET_DOWN);
            intake.setPower(Globals.Intake.POWER_ON);
        }

        if (config.gamepad2.x) {
            bucket.setPosition(Globals.Intake.BUCKET_UP);
            intake.setPower(Globals.Intake.POWER_OFF);
        }

        if (config.gamepad2.y) {
            bucket.setPosition(Globals.Intake.BUCKET_DUMP);
            intake.setPower(Globals.Intake.POWER_DUMP);
        }

        if (config.gamepad2.dpad_down) {
            bucket.setPosition(Globals.Intake.BUCKET_PURGE);
            intake.setPower(Globals.Intake.POWER_DUMP);
        }

        if (config.gamepad2.dpad_left) {
            intake.setPower(Globals.Intake.POWER_PURGE);
        }
    }

    public Action run() {
        return packet -> {
            switch (status) {
                case DUMPING:
                    return false;
                case RETRACTED:
                    extendo.setPower(Globals.Intake.EXTENDO_POWER_OUT);
                    status = IntakePosition.EXTENDING;
                    return true;
                case INTAKING:
                    /*
                    if (sensor.red() >= Globals.Intake.SENSOR_THRESHOLD_RED && sensor.blue() >= Globals.Intake.SENSOR_THRESHOLD_BLUE && sensor.green() >= Globals.Intake.SENSOR_THRESHOLD_GREEN) {
                        bucket.setPosition(Globals.Intake.BUCKET_UP);
                        intake.setPower(Globals.Intake.POWER_OFF);

                        extendo.setPower(Globals.Intake.EXTENDO_POWER_IN);

                        status = IntakePosition.RETRACTING;
                    }

                     */
                    return true;
                case RETRACTING:
                    if (extendo.getCurrentPosition() <= Globals.Intake.EXTENDO_IN) {
                        extendo.setPower(Globals.Intake.EXTENDO_POWER_OFF);
                        bucket.setPosition(Globals.Intake.BUCKET_DUMP);
                        intake.setPower(Globals.Intake.POWER_DUMP);

                        status = IntakePosition.DUMPING;
                    }
                    return true;
                case EXTENDING:
                    if (extendo.getCurrentPosition() >= Globals.Intake.EXTENDO_OUT) {
                        extendo.setPower(Globals.Intake.EXTENDO_POWER_OFF);
                        status = IntakePosition.EXTENDED;
                    }
                    return true;
                case EXTENDED:
                    bucket.setPosition(Globals.Intake.BUCKET_DOWN);
                    intake.setPower(Globals.Intake.POWER_ON);

                    status = IntakePosition.INTAKING;
                    return true;
            }
            return false;
        };
    }
    public InstantAction stopDumping() {
        return new InstantAction(() -> {
            intake.setPower(Globals.Intake.POWER_OFF);
            bucket.setPosition(Globals.Intake.BUCKET_UP);

            status = IntakePosition.RETRACTED;
        });
    }
}
