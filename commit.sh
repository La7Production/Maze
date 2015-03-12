git add --all

echo 'Tag? Tapez no pour aucun'
read tagC
echo 'Message de commit : '
read messageC


if [ $tagC == 'no' ]; then
	git commit -m "$messageC"
	git push origin master
	echo 'push terminé avec succès !'
else
	git tag $tagC	
	git commit -m "$messageC"
	git push origin master	
	git push --tag
	echo 'push terminé et tag appliqué !'
fi
exit
