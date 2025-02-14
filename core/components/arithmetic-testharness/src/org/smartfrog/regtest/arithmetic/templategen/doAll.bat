@echo off
setlocal
call %SFHOME%\bin\setClassPath
java org.smartfrog.regtest.arithmetic.templategen.ExampleGen -d 0.9 -o example1TH.sf -t example.vm
java org.smartfrog.regtest.arithmetic.templategen.ExampleGen -d 0.9 -o example2TH.sf -t example.vm
java org.smartfrog.regtest.arithmetic.templategen.ExampleGen -d 0.93 -o example3TH.sf -t example.vm
java org.smartfrog.regtest.arithmetic.templategen.ExampleGen -d 0.93 -o example4TH.sf -t example.vm

echo "#include \"org/smartfrog/regtest/arithmetic/templategen/example1TH.sf\"" > foo1
echo "#include \"org/smartfrog/regtest/arithmetic/templategen/example2TH.sf\"" > foo2
echo "#include \"org/smartfrog/regtest/arithmetic/templategen/example3TH.sf\"" > foo3
echo "#include \"org/smartfrog/regtest/arithmetic/templategen/example4TH.sf\"" > foo4

echo "#include \"org/smartfrog/regtest/arithmetic/templategen/hostTemplate1.sf\"" > host1
echo "#include \"org/smartfrog/regtest/arithmetic/templategen/hostTemplate2.sf\"" > host2

cat foo1 host1 > example1TH_1.sf
cat foo1 host2 > example1TH_2.sf
cat foo2 host1 > example2TH_1.sf
cat foo2 host2 > example2TH_2.sf
cat foo3 host1 > example3TH_1.sf
cat foo3 host2 > example3TH_2.sf
cat foo4 host1 > example4TH_1.sf
cat foo4 host2 > example4TH_2.sf
