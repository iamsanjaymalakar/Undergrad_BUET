%using graphical model
format long;
x=-1:.001:2;
y=func_1505057(x);
plot(x,y);
xlabel('mole fraction x');
ylabel('f(x)');
title('Graphical model');
grid on;


% false position method 
[x,iter]=fp_1505057(@func_1505057,0.1,0.0,0.5,1000);
display(x);
display(iter);

% secant method 
[root,iter]=secant_1505057(@func_1505057,-0.45,-0.3,0.5,50);
display(root);
display(iter);