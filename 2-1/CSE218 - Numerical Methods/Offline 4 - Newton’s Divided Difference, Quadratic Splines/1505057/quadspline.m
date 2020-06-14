function quadspline (x,y,tx,ty)

n = length(x)-1;

% co ef matrix
A=[0;zeros(2*n,1);zeros(n-1,1)];
% rhs
b=zeros(length(A),length(A));
j=1;
f=1;
% 2n points
for i=2:2:2*n    
    A(i,f:f+2)=[x(j)^2 x(j) 1];
    b(i)=y(j);
    j=j+1;
    A(i+1,f:f+2)=[x(j)^2 x(j) 1];  
    b(i+1)= y(j);
    f=f+3;
end


% derivatives
j=1;
l=2;
for i=2*n+2:3*n
    A(i,j:j+1)=[2*x(l) 1];
    A(i,j+3:j+4)=[-2*x(l) -1];
    j=j+3;
    l=l+1;
end

% Adjusting the value of a1 to be zero "Linear Spline"
A(1,1)=1;

c=A\b;
j=1;
hold on;
for i=1:n
    curve=@(l) c(j)*l.^2+c(j+1)*l+c(j+2);
    ezplot(curve,[x(i),x(i+1)]);
    hold on
    j=j+3;
end

scatter(x,y,50,'r','filled')
grid on;
xlim([min(x)-2 max(x)+2]);
ylim([min(y)-2 max(y)+2]);

% error calc
error=zeros(1,length(tx));
for i = 1 : length(tx)
    f=1;
    for j = 1 : length(x)-1
        error(i)=0;
        if(tx(i)>=x(j) & tx(i)<=x(j+1))
            % fprintf('%f %f %d\n',ty(i),(c(f)*tx(i).^2+c(f+1)*tx(i)+c(f+2)),f);
            error(i) = (ty(i)-(c(f)*tx(i).^2+c(f+1)*tx(i)+c(f+2)))/ty(i);
            error(i) = abs(error(i)) * 100;
            break;
        end
        f=f+3;
    end
end

error = sum(error)/length(tx);

fprintf('error : %f\n',error);

% scattering test points
scatter(tx,ty);

end