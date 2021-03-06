% Author: Luis A. Gonzalez
% Date	: 23-09-2014
% Brief	: Matlab script. Accel data to Features of a folder
% Reads/store all dataset, calculates all features of the Sample only
% Start and Windowframe need to be set, change after first vizualization.
% Returns 'Results.txt'

clearvars 

%% Settings
    % Defined path of Sets and output file, Folder of Sets must not contain
    % anything else than Sets, the data should be  '%d %d %f %f %f' or else
    % to be changed in line 65

    setspath  = 'C:\Users\LG\Dropbox\SENSORS\BrushT1';
    resultspath = 'C:\Users\LG\Dropbox\SENSORS\BrushResults.txt';

    %Precision of the results Int and decimal point and FFT
    pNum='4';
    pDec='4';
    fftN=200; % FFT resolution biggger the more resolution

    % Sample window
    LowerLimit=1; %Min 1
    WindowSize=200;
    
    %Choose Features 1 is Enable
    ftTDomain=1;        %mean 3x, std dev 3x, variance 3,x & Magnitudes of this values
    ftTCovariance=0;    %2x2 matrices of covariance relations xy,yz,zx.
    ftFFT=0;            %fast furier transform

%% Writting header in Results
UpperLimit=LowerLimit+WindowSize;

list    = dir(fullfile(setspath, '*.txt'));
nFiles   = length(list);

fileID = fopen(resultspath,'w');
fprintf(fileID,'%s\t','File');

if(ftTDomain==1)
    fprintf(fileID,'%s\t%s\t%s\t','mean X','mean Y','mean Z');
    fprintf(fileID,'%s\t%s\t%s\t','std X','std Y','std Z');
    fprintf(fileID,'%s\t%s\t%s\t','var X','var Y','var Z');
    fprintf(fileID,'%s\t%s\t%s\t','Mean Mag','Std Mag','Var Mag');
else
end

if(ftTCovariance==1)
    fprintf(fileID,'%s\t%s\t%s\t','cov XY(1,1)','cov XY(1,2)','cov XY(2,1)','cov XY(2,2)');
    fprintf(fileID,'%s\t%s\t%s\t','cov YZ(1,1)','cov YZ(1,2)','cov YZ(2,1)','cov YZ(2,2)');
    fprintf(fileID,'%s\t%s\t%s\t','cov ZX(1,1)','cov ZX(1,2)','cov ZX(2,1)','cov ZX(2,2)');
    
else
end
    fprintf(fileID,'\n');
fclose(fileID);
fprintf('Processed: \n');
for k = 1:nFiles
  filename = list(k).name;
%% Proper function analyzed
FileToRead=fullfile(setspath,filename);

fileID = fopen(FileToRead,'r');

formatSpec = strcat('%d %d %f %f %f');
sizeM = [5 Inf];

% Matrix_R, contains the dataset.
Matrix_R = fscanf(fileID,formatSpec,sizeM);
fclose(fileID);

% Change transpose
Matrix_R = Matrix_R';  

% All features 

if(size(Matrix_R)<UpperLimit)
    LimitSize=size(Matrix_R);
    UpperLimit=LimitSize(1);
end

Sample = zeros(UpperLimit-LowerLimit,4);
 
for i=(1:UpperLimit-LowerLimit)
    
    Sample(i,1) = Matrix_R(LowerLimit+i,2);
    Sample(i,2) = Matrix_R(LowerLimit+i,3);
    Sample(i,3) = Matrix_R(LowerLimit+i,4);
    Sample(i,4) = Matrix_R(LowerLimit+i,5);
    
    NormalSample(i) = sqrt(power(Sample(i,2),2)+power(Sample(i,3),2)+power(Sample(i,4),2));
    

    
end;

% Time Domain Features
%----------------------------------
% Mean, Standard Deviation, Variance, Covariance and their Magnitudes (norm)
stdX=std(Sample(:,2));
stdY=std(Sample(:,3));
stdZ=std(Sample(:,4));

meanX=mean(Sample(:,2));
meanY=mean(Sample(:,3));
meanZ=mean(Sample(:,4));

varianceX=var(Sample(:,2));
varianceY=var(Sample(:,3));
varianceZ=var(Sample(:,4));

covXY=cov(Sample(:,2),Sample(:,3));
covYZ=cov(Sample(:,3),Sample(:,4));
covZX=cov(Sample(:,4),Sample(:,2));
    
%regression line of Covariance relation: y=mx +b
% http://facultyweb.berry.edu/vbissonnette/statshw/doc/reg_sup.html
    Reg_XY_b=covXY/varianceX;   
    Reg_XY_a=meanY-Reg_XY_b*meanX;

    Reg_YZ_b=covYZ/varianceY;
    Reg_YZ_a=meanZ-Reg_YZ_b*meanY;

    Reg_ZX_b=covZX/varianceZ;
    Reg_ZX_a=meanX-Reg_ZX_b*meanZ;

mMagnitude=sqrt(power(meanX,2)+power(meanY,2)+power(meanZ,2));
stdMagnitude=sqrt(power(stdX,2)+power(stdY,2)+power(stdZ,2));
varMagnitude=sqrt(power(varianceX,2)+power(varianceY,2)+power(varianceZ,2));


fileID = fopen(resultspath,'a');


precisionprint=strcat('%',pNum,'.',pDec,'f\t','%',pNum,'.',pDec,'f\t','%',pNum,'.',pDec,'f\t');


%Printing First name of file and therefore the identifier (output Y)
Yfile=strsplit(filename,'_');

fprintf(fileID,'%s\t',char(Yfile(2)));

if(ftTDomain==1)
    fprintf(fileID,precisionprint,meanX,meanY,meanZ);
    fprintf(fileID,precisionprint,stdX,stdY,stdZ);
    fprintf(fileID,precisionprint,varianceX,varianceY,varianceZ);
    fprintf(fileID,precisionprint,mMagnitude,stdMagnitude,varMagnitude);
else
end

if(ftTCovariance==1)
    fprintf(fileID,precisionprint,covXY(1,1),covXY(2,1),covXY(1,2),covXY(2,2));
    fprintf(fileID,precisionprint,covXY(1,1),covXY(2,1),covXY(1,2),covXY(2,2));
    fprintf(fileID,precisionprint,covXY(1,1),covXY(2,1),covXY(1,2),covXY(2,2));
   
else
end
 fprintf(fileID,'\n');

fclose(fileID);

% Frequency Domain Features TBW
%----------------------------------

% fftF = [0:fftN-1]/fftN;
% 
% fftX=fft(Sample(:,2),fftN);
% fftY=fft(Sample(:,3),fftN);
% fftZ=fft(Sample(:,4),fftN);
% 
% ftplotX=fftshift(abs(fftX));
% ftplotY=fftshift(abs(fftY));
% ftplotZ=fftshift(abs(fftZ));


% %%TO CHECK
%Spectral Entropy
%    P=sum(abs(fft(data-window)).^2)
%    %Normalization
%    d=P(:);
%    d=d/sum(d+ 1e-12);
% 
%    %Entropy Calculation
%    logd = log2(d + 1e-12);
%    Entropy(inc) = -sum(d.*logd)/log2(length(d));

fprintf('%s\t With\t %d lines \n', filename,UpperLimit);

end

