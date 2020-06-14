% graphically solve

% Maximize f(x, y) = 1.75x+1.25y
% subject to
% 1.2x + 2.25y ? 14
% x + 1.1y ? 8
% 2.5x + y ? 9
% x ? 0
% y ? 0

% 1.2x + 2.25y  = 14
% y = (-1.2/2.25)x + 14/2.25

c = 14/2.25;
m = - 1.2/2.25;
x= linspace(0,50); % Adapt n for resolution of graph
y= m*x + c;
ylim([0,15]);
plot(x,y);  
grid on ; 
grid minor ;

hold on;

% x + 1.1 y = 8 
% y = (-1/1.1)x + 8/1.1

c = 8/1.1;
m = - 1/1.1;
x= linspace(0,50); % Adapt n for resolution of graph
y= m*x + c;
ylim([0,15]);
plot(x,y); 

hold on;

% 2.5x + y = 9

c = 9;
m = - 2.5;
x= linspace(0,50); % Adapt n for resolution of graph
y= m*x + c;
ylim([0,20]);
plot(x,y);

hold on;

% getting intersection points 
f = @(x) (-1/1.1)*x +8/1.1;        %defines a function f(x)
g = @(x) 9-2.5*x; %defines a function g(x)

%solve f==g
xroot = fzero(@(x)f(x)-g(x),0.5); %starts search from x==0.5

plot(xroot,f(xroot),'ro');
disp('Intersection point of 2nd and 3rd');
fprintf('%f %f\n',xroot,f(xroot));