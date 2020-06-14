x= -2*pi : 0.2 : 2*pi;
y=cos(x);
y1=s1505057_myCos(x,1);
y2=s1505057_myCos(x,3);
y3=s1505057_myCos(x,5);
y4=s1505057_myCos(x,20);
figure;
plot(x,y,x,y1,x,y2,x,y3,x,y4);
xlabel('Angle in radian');
ylabel('y=cos(x)');
title('Cos(x)');
figure;
error=zeros(1,50);
n=1:50;
for i=2:50
error(i)= abs((s1505057_myCos(1.5,i)-s1505057_myCos(1.5,i-1))/(s1505057_myCos(1.5,i)));
display(error(i));
end
plot(n,error);
xlabel('No of terms');
ylabel('Relative approx. error');
title('Relative approx. error of Cos(x)');