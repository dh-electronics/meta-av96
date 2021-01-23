# build the DK2 m4project firmware examples based on the meta-st-stm32mp-addons
# firmware selection
M4_BOARDS = " STM32MP157C-DK2 "

# Temporary Add the AI_Character_Recognition firmware ourself.
# The firmware will be added thanks to meta-st-stm32mp-addons
PROJECTS_LIST_append_stm32mpcommonmx = " \
${@bb.utils.contains('PROJECTS_LIST_DK2', 'STM32MP157C-DK2/Demonstrations/AI_Character_Recognition', 'STM32MP157C-DK2/Demonstrations/AI_Character_Recognition', '', d)} \
"
