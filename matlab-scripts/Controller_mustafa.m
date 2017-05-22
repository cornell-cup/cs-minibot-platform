function [ u1, u2, tracker] = Controller_mustafa( x_pos,y_pos,theta,Xd,Yd,tracker)
    % x_pos is the x position of the robot
    % y_pos is the y position of the robot
    % theta is the direction the robot is heading in (wrt positive x axis)
    % Xd & Yd are arrays containing the waypoints to track
    % tracker is a counter keeping track of the waypoint being tracked.
    
    xd = Xd(tracker);
    yd = Yd(tracker);
    u1 = 0;
    u2 = 0;
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
    tolerance = 0.05;
    d = ((x_pos - xd)^2 + (y_pos-yd)^2)^0.5; 
    if d < tolerance
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
    if(abs(theta-thetaD) <= 0.05)
        u1 = 5;
        u2 = 5;
    elseif (theta-thetaD > 0.05)
        if(theta < thetaD+pi)
        u1 = -5;
        u2 = 5;
        else
        u1 = 5;
        u2 = -5;
        end
    else
        if(thetaD > theta+pi)
        u1 = -5;
        u2 = 5;
        else
        u1 = 5;
        u2 = -5;
        end
    end
end

