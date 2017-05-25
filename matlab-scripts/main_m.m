hold on
[u1, u2, tracker, thetaD] = Controller_mustafa(x_p,y_p,theta,x_vals,y_vals,tracker);

plot(x_vals,y_vals,'ko');
plot(x_p,y_p,'g*');
xlabel('x (m)');
ylabel('y (m)');
legend('waypoints', 'bot position');
