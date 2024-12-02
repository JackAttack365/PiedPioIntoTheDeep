package org.firstinspires.ftc.teamcode.hardware.subsystem;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.robot.enums.CycleTarget;
import org.firstinspires.ftc.teamcode.hardware.robot.Config;
import org.firstinspires.ftc.teamcode.hardware.Globals;

public class Outtake implements SubSystem {
    public enum LiftPosition {
        BOTTOM,
        LOWERING,
        RISING,
        TOP_BASKET,
        TOP_BAR
    }

    Config config;

    private DcMotor lift;

    public LiftPosition position;

    public Outtake(Config config) {this.config = config;}

    @Override
    public void init() {
        lift = config.hardwareMap.get(DcMotor.class, Globals.Outtake.LIFT_MOTOR);

        lift.setDirection(DcMotor.Direction.FORWARD);

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        position = LiftPosition.BOTTOM;
    }

    @Override
    public void start() {
    }

    @Override
    public void update() {
        if (config.gamepad2.back) {
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

       if (config.target == CycleTarget.SAMPLE) {
           if (config.gamepad2.right_trigger >= 0.1) {
               switch (position) {
                   case BOTTOM:
                       lift.setPower(Globals.Outtake.LIFT_POWER);
                       position = LiftPosition.RISING;
                       config.telemetry.addData("POSITION", "RISING");
                   case RISING:
                       if (lift.getCurrentPosition() >= Globals.Outtake.LIFT_TOP_BASKET) {
                           lift.setPower(Globals.Outtake.LIFT_IDLE);
                           position = LiftPosition.TOP_BASKET;
                           config.telemetry.addData("POSITION", "TOP BASKET");
                       } else {
                           config.telemetry.addData("POSITION", "RISING");
                       }
                   case LOWERING:
                       lift.setPower(Globals.Outtake.LIFT_POWER);
                       position = LiftPosition.RISING;
                       config.telemetry.addData("POSITION", "RISING");
                   case TOP_BASKET:
                       lift.setPower(Globals.Outtake.LIFT_IDLE);

                       config.telemetry.addData("POSITION", "TOP BASKET");
               }
           } else if (config.gamepad2.left_trigger >= 0.1) {
               switch (position) {
                   case TOP_BASKET:
                       lift.setPower(-Globals.Outtake.LIFT_POWER);
                       position = LiftPosition.LOWERING;

                       config.telemetry.addData("POSITION", "LOWERING");
                   case LOWERING:
                       if (lift.getCurrentPosition() <= Globals.Outtake.LIFT_BOTTOM) {
                           lift.setPower(0);
                           position = LiftPosition.BOTTOM;
                           config.telemetry.addData("POSITION", "BOTTOM");
                       }
                   case RISING:
                       lift.setPower(-Globals.Outtake.LIFT_POWER);
                       position = LiftPosition.LOWERING;
                       config.telemetry.addData("POSITION", "LOWERING");
                   case BOTTOM:
                       lift.setPower(Globals.Outtake.LIFT_IDLE);
                       config.telemetry.addData("POSITION", "BOTTOM");
               }
           }
       }
       /*

        if (config.target == CycleTarget.SPECIMEN) {
            if (config.gamepad2.right_trigger >= 0.1) {
                switch (position) {
                    case BOTTOM:
                        lift.setPower(Globals.Outtake.LIFT_POWER);
                        position = LiftPosition.RISING;
                    case RISING:
                        if (lift.getCurrentPosition() >= Globals.Outtake.LIFT_TOP_BAR) {
                            lift.setPower(Globals.Outtake.LIFT_IDLE);
                            position = LiftPosition.TOP_BAR;
                        }
                    case LOWERING:
                        lift.setPower(Globals.Outtake.LIFT_POWER);
                        position = LiftPosition.RISING;
                }
            } else if (config.gamepad2.left_trigger >= 0.1) {
                switch (position) {
                    case TOP_BAR:
                        lift.setPower(-Globals.Outtake.LIFT_POWER);
                        position = LiftPosition.LOWERING;
                    case LOWERING:
                        if (lift.getCurrentPosition() <= Globals.Outtake.LIFT_BOTTOM) {
                            lift.setPower(0);
                            position = LiftPosition.BOTTOM;
                        }
                    case RISING:
                        lift.setPower(-Globals.Outtake.LIFT_POWER);
                        position = LiftPosition.LOWERING;
                }
            }
        }
*/
        config.telemetry.addData("Lift Position", lift.getCurrentPosition());
        config.telemetry.addData("Lift Power", lift.getPower());
    }

    public Action bar() {
        return new Action() {
            boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    lift.setPower(1);

                    initialized = true;
                }

                telemetryPacket.put("lift", lift.getCurrentPosition());
                telemetryPacket.put("liftPower", lift.getPower());

                if (lift.getCurrentPosition() >= Globals.Outtake.LIFT_TOP_BAR) {
                    lift.setPower(Globals.Outtake.LIFT_IDLE);

                    position = LiftPosition.TOP_BAR;
                    return false;
                }


                return true;
            }
        };
    }
    public Action bucket() {
        return new Action() {
            boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    lift.setPower(1);

                    initialized = true;
                }

                telemetryPacket.put("lift", lift.getCurrentPosition());
                telemetryPacket.put("liftPower", lift.getPower());

                if (lift.getCurrentPosition() >= Globals.Outtake.LIFT_TOP_BASKET) {
                    lift.setPower(Globals.Outtake.LIFT_IDLE);

                    position = LiftPosition.TOP_BASKET;
                    return false;
                }


                return true;
            }
        };
    }

    public Action down() {
        return new Action() {
            boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    lift.setPower(-1);

                    initialized = true;
                }

                if (lift.getCurrentPosition() <= Globals.Outtake.LIFT_BOTTOM) {
                    lift.setPower(0);

                    position = LiftPosition.BOTTOM;

                    return false;
                }


                return true;
            }
        };
    }
}