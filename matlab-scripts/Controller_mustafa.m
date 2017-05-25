function [ u1, u2, tracker, thetaD] = Controller_mustafa( x_pos,y_pos,theta,Xd,Yd,tracker)
    % x_pos is the x position of the robot
    % y_pos is the y position of the robot
    % theta is the direction the robot is heading in (wrt positive x axis)
    % Xd & Yd are arrays containing the waypoints to track
    % tracker is a counter keeping track of the waypoint being tracked.
    thetaD = 0;
    xd = Xd(tracker);
    yd = Yd(tracker);
    u1 = 0;
    u2 = 0;
    angle_threshold = pi/12;
    distance_threshold = 0.05;
 %% Calculating desired Yaw Angle
    if(xd-x_pos > 0)
        if(yd-y_pos > 0)
            thetaD = atan((yd-y_pos)/(xd - x_pos));
        else
            thetaD = 2*pi + atan((yd-y_pos)/(xd - x_pos));
        end
    else
        thetaD = pi + atan((yd-y_pos)/(xd - x_pos));
    end
%% Iterating to the next waypoint
    d = ((x_pos - xd)^2 + (y_pos-yd)^2)^0.5; 
    if d < distance_threshold
        tracker = tracker + 1;
        if(tracker > length(Xd))
            tracker = 1;
            return; 
        end
        xd = Xd(tracker);
        yd = Yd(tracker);
        if(xd-x_pos > 0)
            if(yd-y_pos > 0)
                thetaD = atan((yd-y_pos)/(xd - x_pos));
            else
                thetaD = 2*pi + atan((yd-y_pos)/(xd - x_pos));
            end
        else
            thetaD = pi + atan((yd-y_pos)/(xd - x_pos));
        end
    end
%% Yaw Control
    if(abs(theta-thetaD) <= angle_threshold)
        u1 = 4;
        u2 = 4;
    elseif (theta-thetaD > angle_threshold)
        if(theta < thetaD+pi)
        u1 = -1.25;
        u2 = 1.25;
        else
        u1 = 1.25;
        u2 = -1.25;
        end
    else
        if(thetaD > theta+pi)
        u1 = -1.25;
        u2 = 1.25;
        else
        u1 = 1.25;
        u2 = -1.25;
        end
    end
%     elseif (theta-thetaD > angle_threshold)
%         u1 = -1.25;
%         u2 = 1.25;
%     else
%         u1 = 1.25;
%         u2 = -1.25;
%     end
end

