function ndd(x,y,trainP,testP,iter)

if(length(x)~=length(y))
    return;
end

n = length(x) ;
c = zeros(n,n) ;

%y to the first column
c(:,1)=y;
% other co-eff
for j = 2:n
    for i= 1:n-j+1
        c(i,j) = (c(i+1,j-1 )-c(i,j-1))/(x(i+j-1)-x(i));
    end
end

testN = size(testP,1);
testX = testP( : , 1 ) ;
testY = testP( : ,2 ) ;


error = 0 ;
for i =1:testN
    prod = 1 ;
    fx = c( 1 ,1 ) ;
    for j = 1:n-1
        prod=prod*(testX(i)-x(j));
        fx = fx + c(1,j+1)*prod;
    end
    error = error+abs(testY(i)-fx);
end
errorP = (error/testN)*100;
fprintf('(Order %d) Error: %f percent\n',iter,errorP) ;

plotX = linspace(-.5,1.1,100);
plotLen = length(plotX) ;
plotY=zeros(plotLen);
for i = 1:plotLen
    prod = 1;
    plotY(i) = c(1,1);
    for j = 1:n-1
        prod = prod*(plotX(i)-x(j)) ;
        plotY(i)=plotY(i)+c(1,j+1)*prod;
    end
end

plot(plotX,plotY);
end