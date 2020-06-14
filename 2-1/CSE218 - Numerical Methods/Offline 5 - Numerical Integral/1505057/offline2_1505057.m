f=@(x) (x.^4-5*x.^2+3*x+1);
df=@(x) (4*x.^3-10*x+3);

x=0:0.5:4;
true=f(x);
xi=0;
yi=-1;
h=0.5;
a=0;
b=4;

grid on;
hold on;

fprintf('Euler\n');
eu=euler(f,df,a,b,h,xi,yi);

fprintf('\n\nHuen\n');
hu=huen(f,df,a,b,h,xi,yi);

fprintf('\n\nMidpoint\n');
mi=midpoint(f,df,a,b,h,xi,yi);

fprintf('\n\nRalston\n');
ra=midpoint(f,df,a,b,h,xi,yi);

plot(x,true,x,eu,16,x,hu,x,mi,x,ra);

legend('True','Euler','Heun','Midpoint','Ralston');