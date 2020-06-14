function y=ralston(f,df,a,b,h,xi,yi)
y(1)=yi;
n=(b-a)/h+1;
x=a+h;
fprintf('      x        True     Ralston      Error  \n');
fprintf('  %f   %f   %f   %f  \n',a,yi,yi,0);
for i=2:n
    y(i)=y(i-1)+(df(x-h)+2*df(x-h+3*h/4))*h/3;
    error=(f(x)-y(i))/f(x)*100;
    fprintf('  %f   %f   %f   %f  \n',x,f(x),y(i),error);
    x=x+h;
end
end
    